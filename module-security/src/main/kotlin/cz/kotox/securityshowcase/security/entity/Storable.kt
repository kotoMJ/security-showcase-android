package cz.kotox.securityshowcase.security.entity

interface Storable {
	public fun toStoreString(): String
	public fun updateFromStore(storeString: String)
}