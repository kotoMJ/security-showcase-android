package cz.koto.securityshowcase

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.securityshowcase.utility.Logcat
import cz.koto.securityshowcase.utility.PrefDelegate

open class SecurityApplication : Application() {

	lateinit var keystoreCompat: KeystoreCompat

	override fun onCreate() {
		super.onCreate()
		Logcat.initialize("SecurityShowcase", SecurityConfig.isPackageDebug())
		ContextProvider.initialize(this)
		LeakCanary.install(this)
		PrefDelegate.initialize(this)

		keystoreCompat = KeystoreCompat.getInstance(this, ShowcaseKeystoreCompatConfig())
	}
}

