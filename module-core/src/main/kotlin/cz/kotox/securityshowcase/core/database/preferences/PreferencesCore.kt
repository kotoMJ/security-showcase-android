package cz.kotox.securityshowcase.core.database.preferences

import android.content.Context
import android.content.SharedPreferences
import cz.kotox.securityshowcase.core.OpenForMocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForMocking
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