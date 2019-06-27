package cz.koto.securityshowcase.app_legacylogin

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.ProcessLifecycleOwner
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import cz.koto.securityshowcase.BuildConfig
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.app_legacylogin.di.AppComponentLegacy
import cz.koto.securityshowcase.app_legacylogin.di.DaggerAppComponentLegacy
import cz.koto.securityshowcase.core.BaseApplication
import cz.koto.securityshowcase.core.entity.AppVersionLegacy
import cz.koto.securityshowcase.core.ktools.lazyUnsafe

class SecurityShowcaseLoginApplicationLegacy : BaseApplication(), LifecycleObserver {

	internal val appComponent: AppComponentLegacy by lazyUnsafe {
		DaggerAppComponentLegacy
			.builder()
			.application(this)
			.applicationInterface(this)
			.appVersion(AppVersionLegacy(BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME))
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