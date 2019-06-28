package cz.kotox.keystorecompat.compat

import cz.kotox.keystorecompat.base.compat.KeystoreCompatFacade
import cz.kotox.keystorecompat19.compat.KeystoreCompatK
import cz.kotox.keystorecompat21.compat.KeystoreCompatL
import cz.kotox.keystorecompat23.compat.KeystoreCompatM

class KeystoreCompatImpl(val keystoreCompatConfig: KeystoreCompatConfig) {
	lateinit var keystoreCompat: KeystoreCompatFacade

	@Suppress("MagicNumber")
	fun init(version: Int) = when {
		version >= 23 -> keystoreCompat = KeystoreCompatM(keystoreCompatConfig)
		version >= 21 -> keystoreCompat = KeystoreCompatL()
		version >= 19 -> keystoreCompat = KeystoreCompatK()
		else -> throw IllegalAccessException("Unsupported API Version [$version] for KeystoreCompat ")
	}
}
