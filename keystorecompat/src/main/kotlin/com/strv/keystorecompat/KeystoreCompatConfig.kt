package com.strv.keystorecompat

import android.os.Build

open class KeystoreCompatConfig {

    /**
     * How would you explain to the user, that your application needs to access to DeviceAdmin.
     * Related only for API 19, AndroidKitKat.
     */
    open fun getKitkatDeviceAdminExplanatory(): String {
        return "Allow application to enforce Android LOCK screen for secure credentials handling"
    }

    /**
     * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
     */
    open fun getProviderForbidThreshold(): Int {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            return 1 //In case of Admin request dialog on KitKat escape after first cancel click.
        } else {
            return 2 //In case of standard Android security dialog dismiss dialog after second CANCEL button
        }
    }
}