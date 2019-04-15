package cz.kotox.securityshowcase.module_login.database.preferences

import android.content.SharedPreferences
import cz.kotox.securityshowcase.module_core.OpenForMockingLegacy
import cz.kotox.securityshowcase.module_core.database.preferences.LocalPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForMockingLegacy
class PreferencesLoginLegacy @Inject constructor(
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