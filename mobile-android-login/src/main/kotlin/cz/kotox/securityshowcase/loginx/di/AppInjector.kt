package cz.kotox.securityshowcase.loginx.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import cz.kotox.securityshowcase.core.di.Injectable
import cz.kotox.securityshowcase.loginx.SecurityShowcaseLoginApplication
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 */
object AppInjector {
	fun init(appOnScreenNavApplication: SecurityShowcaseLoginApplication) {

		DaggerAppComponent
			.builder()
			.application(appOnScreenNavApplication)
			.build()
			.inject(appOnScreenNavApplication)

		appOnScreenNavApplication.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				handleActivity(activity)
			}

			override fun onActivityStarted(activity: Activity) {

			}

			override fun onActivityResumed(activity: Activity) {

			}

			override fun onActivityPaused(activity: Activity) {

			}

			override fun onActivityStopped(activity: Activity) {

			}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

			}

			override fun onActivityDestroyed(activity: Activity) {

			}
		})
	}

	private fun handleActivity(activity: Activity) {
		if (activity is Injectable) {
			AndroidInjection.inject(activity)
		}
		if (activity is HasSupportFragmentInjector) {
			AndroidInjection.inject(activity)
		}
		(activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
			object : FragmentManager.FragmentLifecycleCallbacks() {
				override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
					if (f is Injectable) {
						AndroidSupportInjection.inject(f)
					}
				}
			}, true)
	}
}