package cz.koto.misak.securityshowcase.model

import com.google.gson.annotations.SerializedName

data class AuthRequestSimple(
        @SerializedName("username") var mUsername: String? = null,
        @SerializedName("password") var mPassword: String? = null)


data class AuthResponseSimple(
        val username: String? = null,
        val userId: String? = null,
        val token: String? = null,
        val successful: Boolean = false)
