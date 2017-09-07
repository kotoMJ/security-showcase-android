package cz.koto.securityshowcase.storage

import cz.koto.securityshowcase.utility.Logcat


object CredentialStorage {

	private var accessToken: String? = null
	private var userName: String? = null
	private var password: String? = null

	var forceLockScreenFlag: Boolean? = true

	fun getAccessToken(): String? {
		if (accessToken != null)
			Logcat.d("getToken %s", accessToken!!)
		else
			Logcat.d("NULL token!")
		return accessToken
	}

	fun getUserName() = userName
	fun getPassword() = password

	fun storeUser(token: String, username: String, pass: String) {
		accessToken = token
		userName = username
		password = pass
	}

	fun performLogout() {
		accessToken = null
		userName = null
		password = null
	}

	/**
	 * Set forceLockScreenFlag to avoid automatic login just after logout.
	 */
	fun forceLockScreenFlag() {
		forceLockScreenFlag = true
	}

	/**
	 * Dismiss requirement to display LockScreen given by application.
	 * Requirement given by certificate definition remains.
	 */
	fun dismissForceLockScreenFlag() {
		this.forceLockScreenFlag = false
	}

}