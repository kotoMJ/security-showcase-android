package cz.koto.misak.securityshowcase

import com.strv.keystorecompat.KeystoreCompatConfig

/**
 * Define you own confi to be able override default KeystoreCompat configuration
 */
class ShowcaseKeystoreCompatConfig : KeystoreCompatConfig() {

    override fun getKitkatDeviceAdminExplanatory(): String {
        return "OVERRIDEN EXPLANATORY"
    }
}