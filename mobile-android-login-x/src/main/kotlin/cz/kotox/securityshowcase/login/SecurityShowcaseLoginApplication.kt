package cz.kotox.securityshowcase.login

import android.os.Bundle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavDeepLinkBuilder
import cz.kotox.securityshowcase.BuildConfig
import cz.kotox.securityshowcase.R
import cz.kotox.securityshowcase.core.BaseApplication
import cz.kotox.securityshowcase.core.entity.AppVersion
import cz.kotox.securityshowcase.core.ktools.lazyUnsafe
import cz.kotox.securityshowcase.login.di.AppComponent
import cz.kotox.securityshowcase.login.di.DaggerAppComponent

class SecurityShowcaseLoginApplication : BaseApplication(), LifecycleObserver {

	internal val appComponent: AppComponent by lazyUnsafe {
		DaggerAppComponent
			.builder()
			.application(this)
			.applicationInterface(this)
			.appVersion(AppVersion(BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME))
			.build()
	}

	override fun onCreate() {
		super.onCreate()
		appComponent.inject(this)

		// run all init actions from all modules
		appComponent.initActions.forEach { it.invoke() }

		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	override fun navigateHome() {
		val homePendingIntent = NavDeepLinkBuilder(this)
			.setGraph(R.navigation.mobile_navigation)
			.setDestination(R.id.launcher_home)
			.createPendingIntent()
		homePendingIntent.send()
	}

	override fun redirectToLogin(args: Bundle?) {
		val homePendingIntent = NavDeepLinkBuilder(this)
			.setGraph(R.navigation.login_navigation)
			.setDestination(R.id.login_screen)
			.createPendingIntent()
		homePendingIntent.send()
	}

	override fun crashlyticsLogException(e: Throwable) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun crashlyticsLogMessage(message: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}