package cz.koto.keystorecompat.elplus.compat

import android.os.Build
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat21.compat.KeystoreCompatL
import cz.koto.keystorecompat23.compat.KeystoreCompatM

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
		keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
	} else if (version >= Build.VERSION_CODES.LOLLIPOP) {
		keystoreCompat = KeystoreCompatL()
	} else {
		throw RuntimeException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
