package cz.koto.securityshowcase.api.rest

import cz.koto.securityshowcase.model.AuthRequestSimple
import cz.koto.securityshowcase.model.AuthResponseSimple
import io.reactivex.Maybe
import retrofit2.http.Body
import retrofit2.http.POST


interface SecurityShowcaseRestInterface {

	@POST("/api/securityshowcase/jwtLogin")
	fun loginJWT(@Body authRequest: AuthRequestSimple): Maybe<AuthResponseSimple>

}
