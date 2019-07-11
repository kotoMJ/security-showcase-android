package cz.kotox.securityshowcase.login.database.preference

import android.content.SharedPreferences
import cz.kotox.securityshowcase.core.OpenForMocking
import cz.kotox.securityshowcase.core.database.preferences.LocalPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForMocking
class PreferencesLogin @Inject constructor(
	private val sharedPreferences: SharedPreferences
) : LocalPreferences {
	companion object {
		private const val PREFS_LOGIN_ENCRYPTED_PASSWORD = "prefs_login_encrypted_password"
		private const val PREFS_LOGIN_EMAIL = "prefs_login_email"
	}

	override fun clearForSignOut() {
		// do nothing
	}

	var encryptedPassword: String?
		get() = sharedPreferences.getString(PREFS_LOGIN_ENCRYPTED_PASSWORD, null)
		set(encryptedPassword) {
			sharedPreferences.edit().putString(PREFS_LOGIN_ENCRYPTED_PASSWORD, encryptedPassword).apply()
		}

	var loginEmail: String?
		get() = sharedPreferences.getString(PREFS_LOGIN_EMAIL, null)
		set(loginEmail) {
			sharedPreferences.edit().putString(PREFS_LOGIN_EMAIL, loginEmail).apply()
		}
}
