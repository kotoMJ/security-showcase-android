package cz.kotox.securityshowcase.core.ktools

import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class AnkoAsyncContext<T>(val weakRef: WeakReference<T>)

fun <T> AnkoAsyncContext<T>.uiThread(f: (T) -> Unit): Boolean {
	val ref = weakRef.get() ?: return false
	if (ContextHelper.mainThread == Thread.currentThread()) {
		f(ref)
	} else {
		ContextHelper.handler.post { f(ref) }
	}
	return true
}

fun <T> T.doAsync(
	exceptionHandler: ((Throwable) -> Unit)? = null,
	task: AnkoAsyncContext<T>.() -> Unit
): Future<Unit> {
	val context = AnkoAsyncContext(WeakReference(this))
	return BackgroundExecutor.submit {
		try {
			context.task()
		} catch (thr: Throwable) {
			exceptionHandler?.invoke(thr) ?: Unit
		}
	}
}

internal object BackgroundExecutor {
	var executor: ExecutorService =
		Executors.newScheduledThreadPool(2 * Runtime.getRuntime().availableProcessors())

	fun <T> submit(task: () -> T): Future<T> {
		return executor.submit(task)
	}

}

private object ContextHelper {
	val handler = Handler(Looper.getMainLooper())
	val mainThread = Looper.getMainLooper().thread
}

/**
 * Shortcut for lazy with no thread safety
 */
fun <T> lazyUnsafe(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
