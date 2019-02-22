package cz.kotox.securityshowcase.core

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

open class PreferencesCore @Inject constructor(
	val context: Context,
	private val sharedPreferences: SharedPreferences
) {

	companion object {
		const val PREFS_SAMPLE_TOKEN = "prefs_sample_token"
		const val PREFS_DEFAULT_VALUE = "prefs_default_value"
	}

	open var sampleToken: String
		get() = sharedPreferences.getString(PREFS_SAMPLE_TOKEN, PREFS_DEFAULT_VALUE) ?: PREFS_DEFAULT_VALUE
		set(userId) {
			sharedPreferences.edit().putString(PREFS_SAMPLE_TOKEN, userId).apply()
		}

	open fun clearSampleToken() {
		sharedPreferences.edit().putString(PREFS_SAMPLE_TOKEN, null).apply()
	}

}