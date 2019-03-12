package cz.kotox.securityshowcase.core.database.preferences

import android.content.Context
import android.content.SharedPreferences
import cz.kotox.securityshowcase.core.OpenForMocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForMocking
open class PreferencesCommon @Inject constructor(
	val context: Context,
	private val sharedPreferences: SharedPreferences
) : LocalPreferences {

	companion object {
		private const val PREFS_JWT_TOKEN = "prefs_sample_token"
		private const val PREFS_DEFAULT_VALUE = "prefs_default_value"
		private const val PREFS_USER_ID_TOKEN = "prefs_user_id_token"
		const val ID_TOKEN_DEFAULT_VALUE = -1L
	}

	var userId: Long
		get() = sharedPreferences.getLong(PREFS_USER_ID_TOKEN, ID_TOKEN_DEFAULT_VALUE)
		set(userId) {
			sharedPreferences.edit().putLong(PREFS_USER_ID_TOKEN, userId).apply()
		}

	fun clearUserId() {
		sharedPreferences.edit().putLong(PREFS_USER_ID_TOKEN, ID_TOKEN_DEFAULT_VALUE).apply()
	}

	open var jwtToken: String
		get() = sharedPreferences.getString(PREFS_JWT_TOKEN, PREFS_DEFAULT_VALUE) ?: PREFS_DEFAULT_VALUE
		set(userId) {
			sharedPreferences.edit().putString(PREFS_JWT_TOKEN, userId).apply()
		}

	open fun clearJwtToken() {
		sharedPreferences.edit().putString(PREFS_JWT_TOKEN, null).apply()
	}

	override fun clearForSignOut() {
		clearJwtToken()
		clearUserId()
	}

}