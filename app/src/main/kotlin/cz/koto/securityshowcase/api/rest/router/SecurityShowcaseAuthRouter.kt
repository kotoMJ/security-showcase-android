package cz.koto.securityshowcase.api.rest.router

import cz.koto.securityshowcase.model.AuthRequestSimple
import cz.koto.securityshowcase.model.AuthResponseSimple
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST


interface SecurityShowcaseAuthRouter {

	@POST("/api/securityshowcase/jwtLogin")
	fun loginJWT(@Body authRequest: AuthRequestSimple): Single<AuthResponseSimple>

}
