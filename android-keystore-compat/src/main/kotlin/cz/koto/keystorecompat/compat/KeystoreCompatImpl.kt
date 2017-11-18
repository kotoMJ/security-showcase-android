package cz.koto.keystorecompat.compat

import android.os.Build
import cz.koto.keystorecompat.base.compat.KeystoreCompatConfig
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat19.compat.KeystoreCompatK
import cz.koto.keystorecompat23.compat.KeystoreCompatM

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
		keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
	} else if (version >= Build.VERSION_CODES.LOLLIPOP) {
		keystoreCompat = KeystoreCompatL
	} else if (version >= Build.VERSION_CODES.KITKAT) {
		keystoreCompat = KeystoreCompatK()
	} else {
		throw RuntimeException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
