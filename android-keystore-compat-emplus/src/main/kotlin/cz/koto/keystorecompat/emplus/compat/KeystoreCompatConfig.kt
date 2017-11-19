package cz.koto.keystorecompat.emplus.compat

import android.annotation.TargetApi
import android.os.Build
import cz.koto.keystorecompat.base.KeystoreCompatConfigBase
import cz.koto.keystorecompat23.compat.KeystoreCompatConfigM

open class KeystoreCompatConfig : KeystoreCompatConfigM, KeystoreCompatConfigBase {

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

	/**
	 * User has to type challenge in 10 seconds.
	 * He will be challenged with the lock-screen otherwise
	 * This settings is working since Android M
	 */
	@TargetApi(Build.VERSION_CODES.M) override fun getUserAuthenticationValidityDurationSeconds(): Int = 10

	/**
	 * Sets whether this key is authorized to be used only if the user has been authenticated
	 * False value could allow to avoid lock-screen during saving secret key
	 * Even with false value you still can force lock-screen
	 * This settings is working since Android M
	 */
	@TargetApi(Build.VERSION_CODES.M) override fun getUserAuthenticationRequired(): Boolean = true

}