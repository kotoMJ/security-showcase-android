package cz.kotox.securityshowcase.core.arch.event

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

// source: https://github.com/googlesamples/android-architecture-components/issues/63
class LiveEvent<T> : MutableLiveData<T>() {
	private val pending = AtomicBoolean(false)

	@MainThread
	override fun observe(@NonNull lifecycleOwner: LifecycleOwner, @NonNull observer: Observer<in T>) {
		// observe the internal MutableLiveData
		super.observe(lifecycleOwner, Observer<T> { value ->
			if (pending.compareAndSet(true, false)) {
				observer.onChanged(value)
			}
		})
	}

	@MainThread
	override fun setValue(@Nullable value: T?) {
		pending.set(true)
		super.setValue(value)
	}

	@MainThread
	fun call() {
		setValue(null)
	}
}
