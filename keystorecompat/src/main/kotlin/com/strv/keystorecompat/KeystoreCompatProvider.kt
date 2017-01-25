package com.strv.keystorecompat

import android.os.Build

internal object KeystoreCompatProvider {
    lateinit var keystoreCompat: KeystoreCompatFacade

    fun init(version: Int) = if (version >= Build.VERSION_CODES.M) {
        keystoreCompat = KeystoreCompatM
    } else if (version >= Build.VERSION_CODES.LOLLIPOP) {
        keystoreCompat = KeystoreCompatL
    } else if (version >= Build.VERSION_CODES.KITKAT) {
        keystoreCompat = KeystoreCompatK
    } else {
        throw RuntimeException("Unsupported API Version [$version] for KeystoreCompat ")
    }
}
