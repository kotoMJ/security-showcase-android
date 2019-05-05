package cz.kotox.securityshowcase.login

import android.content.Intent
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
import cz.kotox.securityshowcase.login.ui.MainActivity

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

	override fun navigateHomeScreen() {
		val homePendingIntent = NavDeepLinkBuilder(this)
			.setGraph(R.navigation.mobile_navigation)
			.setDestination(R.id.launcher_home)
			.createPendingIntent()
		homePendingIntent.send()
	}

	override fun startApp() {
		val goHomeIntent = Intent(this, MainActivity::class.java)
		goHomeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(Intent(goHomeIntent))
	}

	override fun redirectToLogin(args: Bundle?) {
//		val homePendingIntent = NavDeepLinkBuilder(this)
////			.setGraph(R.navigation.login_navigation)
////			.setDestination(R.id.login_screen)
////			.createPendingIntent()
////		homePendingIntent.send()
		val goToLoginIntent = Intent(this, LoginActivity::class.java)
		goToLoginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(Intent(goToLoginIntent))
	}

	override fun crashlyticsLogException(e: Throwable) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun crashlyticsLogMessage(message: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}