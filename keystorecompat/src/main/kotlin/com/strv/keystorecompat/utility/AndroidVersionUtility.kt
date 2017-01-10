package com.strv.keystorecompat.utility

import android.os.Build


inline fun runSinceLollipop(crossinline action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 21) action()
}

inline fun runSinceMarshmallow(crossinline action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= 23) action()
}
