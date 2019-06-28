package cz.kotox.keystorecompat.compat

import android.os.Build
import cz.kotox.keystorecompat.base.compat.KeystoreCompatFacade
import cz.kotox.keystorecompat19.compat.KeystoreCompatK
import cz.kotox.keystorecompat21.compat.KeystoreCompatL
import cz.kotox.keystorecompat23.compat.KeystoreCompatM

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
		keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
	} else if (version >= Build.VERSION_CODES.LOLLIPOP) {
		keystoreCompat = KeystoreCompatL()
	} else if (version >= Build.VERSION_CODES.KITKAT) {
		keystoreCompat = KeystoreCompatK()
	} else {
		throw IllegalAccessException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
