package cz.koto.misak.securityshowcase.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import java.util.*

val Context.inputMethodManager: InputMethodManager get() =
getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.deviceID() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

fun installationId(preferences: SharedPreferences): UUID {
    var id = preferences.getString("installationId", null)
    if (id == null) {
        id = UUID.randomUUID().toString()
        preferences["installationId"] = id
    }
    return UUID.fromString(id)
}

operator fun SharedPreferences.set(key: String, value: String) = edit().putString(key, value).commit()

fun Context.dp(px: Int) = (px / (resources.displayMetrics.densityDpi / 160f)).toInt()

inline fun <reified A : Activity> Context.start(config: Intent.() -> Unit) =
        startActivity(android.content.Intent(this, A::class.java).apply(config))