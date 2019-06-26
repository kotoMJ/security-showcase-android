package cz.kotox.keystorecompat23.compat

import android.annotation.TargetApi
import android.os.Build

interface KeystoreCompatConfigM {

	/**
	 * User has to type challenge in 10 seconds.
	 * He will be challenged with the lock-screen otherwise
	 * This settings is working since Android M
	 */
	@TargetApi(Build.VERSION_CODES.M)
	fun getUserAuthenticationValidityDurationSeconds(): Int

	/**
	 * Sets whether this key is authorized to be used only if the user has been authenticated
	 * False value could allow to avoid lock-screen during saving secret key
	 * Even with false value you still can force lock-screen
	 * This settings is working since Android M
	 */
	@TargetApi(Build.VERSION_CODES.M)
	fun getUserAuthenticationRequired(): Boolean

}