package cz.kotox.keystorecompat.base

import android.os.Build


interface KeystoreCompatConfigBase {

	fun isRootDetectionEnabled(): Boolean = true

	/**
	 * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
	 */
	open fun getDialogDismissThreshold(): Int {
		return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			1 //In case of Admin request dialog on KitKat escape after first cancel click.
		} else {
			1 //In case of standard Android security dialog dismiss dialog after first CANCEL button click.
		}
	}

}