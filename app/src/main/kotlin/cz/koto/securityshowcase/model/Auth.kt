package cz.koto.securityshowcase.model

import com.google.gson.annotations.SerializedName

data class AuthRequestSimple(
		@SerializedName("email") var mEmail: String? = null,
		@SerializedName("password") var mPassword: String? = null)


data class AuthResponseSimple(
		@SerializedName("id_token") val idToken: String? = null)
