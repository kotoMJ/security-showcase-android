package cz.koto.misak.securityshowcase

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.strv.keystorecompat.KeystoreCompat
import cz.koto.misak.securityshowcase.utility.Logcat
import cz.koto.misak.securityshowcase.utility.PrefDelegate

open class SecurityApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Logcat.initialize("SecurityShowcase", SecurityConfig.isPackageDebug())
        ContextProvider.initialize(this)
        LeakCanary.install(this)
        PrefDelegate.initialize(this)
        KeystoreCompat.init(this)
    }
}

