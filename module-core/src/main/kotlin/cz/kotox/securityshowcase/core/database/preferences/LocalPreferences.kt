package cz.kotox.securityshowcase.core.database.preferences

/**
 * Common interface for all preferences classes.
 * Each class should handle it's own data (so that we have separated responsibility)
 * When user logouts, each class will call [LocalPreferences.clearForSignOut]
 *
 * MUST BE REGISTERED IN MODULE'S DAGGER AND BIND INTO SET
 */
interface LocalPreferences {
	fun clearForSignOut()
}
