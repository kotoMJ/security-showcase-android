package cz.koto.misak.keystorecompat

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

    open fun isRootDetectionEnabled(): Boolean {
        return true
    }
}