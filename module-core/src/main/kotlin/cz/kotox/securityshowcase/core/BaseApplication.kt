package cz.kotox.securityshowcase.core

import android.app.Activity
import android.app.Application
import android.app.Service
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import cz.kotox.securityshowcase.core.logging.timber.CrashReportingTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface ApplicationInterfaceContract {
	fun startApp()
	fun redirectToLogin(args: Bundle? = null)
	fun navigateHomeScreen()
	fun crashlyticsLogException(e: Throwable)
	fun crashlyticsLogMessage(message: String)
}

@Singleton
@OpenForMocking
class AppInterface @Inject constructor(
	private val applicationInterface: ApplicationInterfaceContract
) : LifecycleObserver, ApplicationInterfaceContract by applicationInterface {

	init {
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	//val isAppInForeground = BehaviorRelay.createDefault(false)
	val isAppInForeground = MutableLiveData<Boolean>()

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	internal fun onAppStart() {
		isAppInForeground.postValue(true)
		//isAppInForeground.accept(true)
		Timber.d("APP is in foreground")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	internal fun onAppStop() {
		isAppInForeground.postValue(false)
		//isAppInForeground.accept(false)
		Timber.d("APP is in background")
	}

}

@OpenForMocking
abstract class BaseApplication : Application(), ApplicationInterfaceContract, HasActivityInjector, HasServiceInjector {
	companion object {
		lateinit var instance: BaseApplication
	}

	@Inject
	lateinit var dispatchingActivityAndroidInjector: DispatchingAndroidInjector<Activity>
	@Inject
	lateinit var dispatchingServiceAndroidInjector: DispatchingAndroidInjector<Service>

	override fun activityInjector(): DispatchingAndroidInjector<Activity> = dispatchingActivityAndroidInjector

	override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceAndroidInjector

	override fun onCreate() {
		super.onCreate()
		instance = this

		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		} else {
			Timber.plant(CrashReportingTree())
		}
	}
}