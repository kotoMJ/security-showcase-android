package com.strv.keystorecompat

import android.content.Context
import com.strv.keystorecompat.utility.PrefDelegate
import com.strv.keystorecompat.utility.runSinceLollipop
import com.strv.keystorecompat.utility.stringPref

object KeystoreCompat {
    lateinit var context: Context
    var encryptedUserData by stringPref("secure_pin_data")

    fun initialize(context: Context) {
        this.context = context
        runSinceLollipop { KeystoreProvider.init() }
        PrefDelegate.initialize(context)
    }

}