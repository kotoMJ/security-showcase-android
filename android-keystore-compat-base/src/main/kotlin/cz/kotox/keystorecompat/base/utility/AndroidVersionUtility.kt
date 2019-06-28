package cz.kotox.keystorecompat.base.utility

import android.os.Build


inline fun runSinceKitKat(crossinline action: () -> Unit) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) action()
}

inline fun runSinceLollipop(crossinline action: () -> Unit) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) action()
}

inline fun runSinceMarshmallow(crossinline action: () -> Unit) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) action()
}
