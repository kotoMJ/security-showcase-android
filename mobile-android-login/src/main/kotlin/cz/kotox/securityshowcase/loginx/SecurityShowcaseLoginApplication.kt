package cz.kotox.securityshowcase.loginx

import android.app.Activity
import android.app.Application
import androidx.multidex.MultiDexApplication
import androidx.navigation.NavDeepLinkBuilder
import cz.kotox.routines.BuildConfig
import cz.kotox.routines.R
import cz.kotox.securityshowcase.core.ApplicationInterface
import cz.kotox.securityshowcase.core.FeatureCore
import cz.kotox.securityshowcase.core.logging.timber.CrashReportingTree
import cz.kotox.securityshowcase.loginx.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class SecurityShowcaseLoginApplication : MultiDexApplication(), ApplicationInterface, HasActivityInjector {

	@Inject
	lateinit var dispatchingActivityAndroidInjector: DispatchingAndroidInjector<Activity>

	override fun onCreate() {
		super.onCreate()

		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		} else {
			Timber.plant(CrashReportingTree())
		}

		FeatureCore.init(this)
		AppInjector.init(this)
	}

	override fun navigateHome() {
		val homePendingIntent = NavDeepLinkBuilder(this)
			.setGraph(R.navigation.mobile_navigation)
			.setDestination(R.id.launcher_home)
			.createPendingIntent()
		homePendingIntent.send()
	}

	override fun provideApplication(): Application = this

	override fun getVersionCode() = BuildConfig.VERSION_CODE

	override fun getVersionName() = BuildConfig.VERSION_NAME

	override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityAndroidInjector
}