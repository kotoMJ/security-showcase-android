package cz.koto.misak.keystorecompat

import android.annotation.TargetApi
import android.os.Build

open class KeystoreCompatConfig {

    /**
     * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
     */
    open fun getDialogDismissThreshold(): Int {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return 1 //In case of Admin request dialog on KitKat escape after first cancel click.
        } else {
            return 1 //In case of standard Android security dialog dismiss dialog after first CANCEL button click.
        }
    }

    /**
     * User has to type challenge in 10 seconds.
     * He will be challenged with the lock-screen otherwise
     * This settings is working since Android M
     */
    @TargetApi(Build.VERSION_CODES.M)
    open fun getUserAuthenticationValidityDurationSeconds(): Int {
        return 10
    }

    /**
     * Sets whether this key is authorized to be used only if the user has been authenticated
     * False value could allow to avoid lock-screen during saving secret key
     * Even with false value you still can force lock-screen
     * This settings is working since Android M
     */
    @TargetApi(Build.VERSION_CODES.M)
    open fun getUserAuthenticationRequired(): Boolean {
        return true
    }

    open fun isRootDetectionEnabled(): Boolean {
        return true
    }
}