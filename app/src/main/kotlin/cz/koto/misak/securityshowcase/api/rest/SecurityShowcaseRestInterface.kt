package cz.koto.misak.securityshowcase.api.rest

import cz.koto.misak.securityshowcase.model.AuthRequestSimple
import cz.koto.misak.securityshowcase.model.AuthResponseSimple
import cz.koto.misak.securityshowcase.model.base.ServerResponseObject
import io.reactivex.Maybe
import retrofit2.http.Body
import retrofit2.http.POST


interface SecurityShowcaseRestInterface {

    @POST("/api/securityshowcase/jwtLogin")
    fun loginJWT(@Body authRequest: AuthRequestSimple): Maybe<AuthResponseSimple>

}
