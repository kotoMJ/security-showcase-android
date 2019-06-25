package cz.kotox.securityshowcase.module_core

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Context
import android.support.v4.app.NotificationManagerCompat
import timber.log.Timber

interface ApplicationInterface {
	fun navigateHome()
	fun provideApplication(): Application
	fun getVersionCode(): Int
	fun getVersionName(): String
}

object FeatureCore : LifecycleObserver {

	var isAppInBackground: Boolean = false
		private set

	lateinit var applicationInterface: ApplicationInterface

	val context: Context by lazy { applicationInterface.provideApplication() }

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	internal fun onAppStart() {
		isAppInBackground = false
		Timber.d("APP is in foreground")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	internal fun onAppStop() {
		isAppInBackground = true
		Timber.d("APP is in background")
	}

	fun init(applicationInterface: ApplicationInterface) {
		FeatureCore.applicationInterface = applicationInterface
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	val notificationsEnabled: Boolean get() = NotificationManagerCompat.from(context).areNotificationsEnabled()

	fun getVersionCode() = applicationInterface.getVersionCode()

	fun getVersionName() = applicationInterface.getVersionName()

}