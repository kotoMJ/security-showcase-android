package cz.kotox.securityshowcase.security.entity

class CredentialStorage(
	var accessToken: String? = null,
	var userName: String? = null,
	var password: String? = null,
	var forceLockScreenFlag: Boolean = true
) : Storable {

	companion object {
		const val DELIMITER = ";"
	}

	override fun updateFromStore(storeString: String) {
		storeString.split(DELIMITER).let {
			userName = it[0]
			password = it[1]
		}
	}

	override fun toStoreString(): String = "${userName ?: ""}$DELIMITER${password ?: ""}"

}