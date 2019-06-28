package cz.kotox.keystorecompat.emplus.compat

import android.os.Build
import cz.kotox.keystorecompat.base.compat.KeystoreCompatFacade
import cz.kotox.keystorecompat23.compat.KeystoreCompatM

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
		keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
	} else {
		throw IllegalAccessException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
