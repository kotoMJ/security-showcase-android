package cz.kotox.securityshowcase.login.network

import cz.kotox.securityshowcase.login.entity.LoginRequestSimple
import cz.kotox.securityshowcase.login.entity.LoginResponseSimple
import cz.kotox.securityshowcase.login.entity.ServerResponseObject
import cz.kotox.securityshowcase.login.entity.UserResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthRouter {

	@POST("/api/securityshowcase/jwtLogin")
	fun loginJWT(@Body authRequest: LoginRequestSimple): Deferred<LoginResponseSimple>

	@GET("/api/securityshowcase/secured/user")
	fun getUser(): Deferred<ServerResponseObject<UserResponse>>

}
