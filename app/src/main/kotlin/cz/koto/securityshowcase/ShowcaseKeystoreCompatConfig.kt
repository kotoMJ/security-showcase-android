package cz.koto.securityshowcase

import android.os.Build
import cz.koto.keystorecompat.compat.KeystoreCompatConfig

/**
 * Define you own confi to be able override default KeystoreCompat configuration
 */
class ShowcaseKeystoreCompatConfig : KeystoreCompatConfig() {

	/**
	 * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
	 */
	override open fun getDialogDismissThreshold(): Int {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			return 1 //In case of Admin request dialog on KitKat escape after first cancel click.
		} else {
			return 2 //In case of standard Android security dialog dismiss dialog after second CANCEL button click
		}
	}

	override fun isRootDetectionEnabled(): Boolean {
		if (BuildConfig.DEBUG) {
			return false
		} else
			return super.isRootDetectionEnabled()
	}
}