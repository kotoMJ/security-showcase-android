package cz.koto.keystorecompat.compat

import android.os.Build
import cz.koto.keystorecompat.KeystoreCompatConfig
import cz.koto.keystorecompat.SecurityDeviceAdmin
import cz.koto.keystorecompat_base.compat.KeystoreCompatFacade

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	companion object {
		val KEYSTORE_KEYWORD = "AndroidKeyStore"
	}

	fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
		keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
	} else if (version >= Build.VERSION_CODES.LOLLIPOP) {
		keystoreCompat = KeystoreCompatL
	} else if (version >= Build.VERSION_CODES.KITKAT) {
		keystoreCompat = KeystoreCompatK(SecurityDeviceAdmin())
	} else {
		throw RuntimeException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
