package cz.koto.keystorecompat.base.utility

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64
import kotlin.reflect.KProperty

/*
 * Android Shared Preferences Delegate for Kotlin
 *
 * Usage:
 *
 * PrefDelegate.init(context)
 * ...
 * var accessToken by stringPref(PREFS_ID, "access_token")
 * var appLaunchCount by intPref(PREFS_ID, "app_launch_count", 0)
 * var autoRefreshEnabled by booleanPref("auto_refresh enabled") // using Default Shared Preferences
 *
 */

abstract class PrefDelegate<T>(val prefName: String?, val prefKey: String) {

	companion object {
		private var context: Context? = null

		/**
		 * Initialize PrefDelegate with a Context reference
		 * !! This method needs to be called before any other usage of PrefDelegate !!
		 */
		fun initialize(context: Context) {
			this.context = context
		}
	}

	protected val prefs: SharedPreferences by lazy {
		if (context != null)
			if (prefName != null) context!!.getSharedPreferences(prefName, Context.MODE_PRIVATE) else PreferenceManager.getDefaultSharedPreferences(context!!)
		else
			throw IllegalStateException("Context was not initialized. Call PrefDelegate.init(context) before using it")
	}

	abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
	abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}


fun stringPref(prefKey: String, defaultValue: String? = null) = StringPrefDelegate(null, prefKey, defaultValue)
//fun stringPref(prefName: String, prefKey: String, defaultValue: String? = null) = StringPrefDelegate(prefName, prefKey, defaultValue)
class StringPrefDelegate(prefName: String?, prefKey: String, val defaultValue: String?) : PrefDelegate<String?>(prefName, prefKey) {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getString(prefKey, defaultValue)
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) = prefs.edit().putString(prefKey, value).apply()
}

fun intPref(prefKey: String, defaultValue: Int = 0) = IntPrefDelegate(null, prefKey, defaultValue)
//fun intPref(prefName: String, prefKey: String, defaultValue: Int = 0) = IntPrefDelegate(prefName, prefKey, defaultValue)
class IntPrefDelegate(prefName: String?, prefKey: String, val defaultValue: Int) : PrefDelegate<Int>(prefName, prefKey) {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getInt(prefKey, defaultValue)
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) = prefs.edit().putInt(prefKey, value).apply()
}

fun byteArrayPref(prefKey: String) = byteArrayPrefDelegate(null, prefKey)
//fun booleanPref(prefName: String, prefKey: String, defaultValue: Boolean = false) = BooleanPrefDelegate(prefName, prefKey, defaultValue)
class byteArrayPrefDelegate(prefName: String?, prefKey: String) : PrefDelegate<ByteArray>(prefName, prefKey) {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = Base64.decode(prefs.getString(prefKey, null), Base64.DEFAULT)
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: ByteArray) = prefs.edit().putString(prefKey, Base64.encodeToString(value, Base64.NO_WRAP)).apply()
}

//fun floatPref(prefKey: String, defaultValue: Float = 0f) = FloatPrefDelegate(null, prefKey, defaultValue)
//fun floatPref(prefName: String, prefKey: String, defaultValue: Float = 0f) = FloatPrefDelegate(prefName, prefKey, defaultValue)
//class FloatPrefDelegate(prefName: String?, prefKey: String, val defaultValue: Float) : PrefDelegate<Float>(prefName, prefKey) {
//    override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getFloat(prefKey, defaultValue)
//    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) = prefs.edit().putFloat(prefKey, value).apply()
//}

fun booleanPref(prefKey: String, defaultValue: Boolean = false) = BooleanPrefDelegate(null, prefKey, defaultValue)
//fun booleanPref(prefName: String, prefKey: String, defaultValue: Boolean = false) = BooleanPrefDelegate(prefName, prefKey, defaultValue)
class BooleanPrefDelegate(prefName: String?, prefKey: String, val defaultValue: Boolean) : PrefDelegate<Boolean>(prefName, prefKey) {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getBoolean(prefKey, defaultValue)
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = prefs.edit().putBoolean(prefKey, value).apply()
}

fun longPref(prefKey: String, defaultValue: Long = 0L) = LongPrefDelegate(null, prefKey, defaultValue)
//fun longPref(prefName: String, prefKey: String, defaultValue: Long = 0L) = LongPrefDelegate(prefName, prefKey, defaultValue)
class LongPrefDelegate(prefName: String?, prefKey: String, val defaultValue: Long) : PrefDelegate<Long>(prefName, prefKey) {
	override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getLong(prefKey, defaultValue)
	override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) = prefs.edit().putLong(prefKey, value).apply()
}
//
//fun stringSetPref(prefKey: String, defaultValue: Set<String> = HashSet<String>()) = StringSetPrefDelegate(null, prefKey, defaultValue)
//fun stringSetPref(prefName: String, prefKey: String, defaultValue: Set<String> = HashSet<String>()) = StringSetPrefDelegate(prefName, prefKey, defaultValue)
//class StringSetPrefDelegate(prefName: String?, prefKey: String, val defaultValue: Set<String>) : PrefDelegate<Set<String>>(prefName, prefKey) {
//    override fun getValue(thisRef: Any?, property: KProperty<*>) = prefs.getStringSet(prefKey, defaultValue)
//    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) = prefs.edit().putStringSet(prefKey, value).apply()
//}
