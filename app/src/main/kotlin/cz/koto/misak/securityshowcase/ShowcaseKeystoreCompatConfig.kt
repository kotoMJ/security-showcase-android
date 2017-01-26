package cz.koto.misak.securityshowcase

import cz.koto.misak.keystorecompat.KeystoreCompatConfig

/**
 * Define you own confi to be able override default KeystoreCompat configuration
 */
class ShowcaseKeystoreCompatConfig : KeystoreCompatConfig() {

    override fun getKitkatDeviceAdminExplanatory(): String {
        return "OVERRIDEN EXPLANATORY"
    }
}