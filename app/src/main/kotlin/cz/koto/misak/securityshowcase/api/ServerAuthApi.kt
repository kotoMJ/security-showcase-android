package cz.koto.misak.securityshowcase.api

import cz.koto.misak.securityshowcase.model.AuthRequestSimple
import cz.koto.misak.securityshowcase.model.AuthResponseSimple
import cz.koto.misak.securityshowcase.model.base.ServerResponseObject
import io.reactivex.Maybe
import retrofit2.http.Body
import retrofit2.http.POST


interface SecurityShowcaseInterface {

    @POST("/api/securityshowcase/login")
    fun loginSimple(@Body authRequest: AuthRequestSimple): Maybe<ServerResponseObject<AuthResponseSimple>>

}
