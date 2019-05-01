package cz.kotox.securityshowcase.login.entity

import com.squareup.moshi.Json
import cz.kotox.securityshowcase.login.qualifier.TestAuthEmail
import cz.kotox.securityshowcase.login.qualifier.TestAuthPassword
import javax.inject.Inject

data class LoginRequestSimple(
	@Json(name = "email") var email: String,
	@Json(name = "password") var password: String)

data class LoginResponseSimple(
	@Json(name = "id_token") val idToken: String)

data class UserResponse(
	@Json(name = "email") var email: String,
	@Json(name = "name") var name: String,
	@Json(name = "surname") var surname: String
)

class AuthCredentials @Inject constructor(
	@TestAuthEmail val email: String,
	@TestAuthPassword val password: String
)
