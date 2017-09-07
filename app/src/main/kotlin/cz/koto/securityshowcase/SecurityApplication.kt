package cz.koto.securityshowcase

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.securityshowcase.utility.Logcat
import cz.koto.securityshowcase.utility.PrefDelegate

open class SecurityApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		Logcat.initialize("SecurityShowcase", SecurityConfig.isPackageDebug())
		ContextProvider.initialize(this)
		LeakCanary.install(this)
		PrefDelegate.initialize(this)

		/**
		 * Not necessarily to be set at all (defaults are available inside the library).
		 * Nor necessarily to be set in Application class (enough to set it before first usage of KeystoreCompat)
		 */
		KeystoreCompat.overrideConfig(ShowcaseKeystoreCompatConfig())
	}
}

