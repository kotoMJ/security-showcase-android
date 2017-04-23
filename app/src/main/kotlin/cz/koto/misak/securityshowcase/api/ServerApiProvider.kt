package cz.koto.misak.securityshowcase.api

import SecurityShowcaseRetrofitProvider
import cz.koto.misak.securityshowcase.api.rest.SecurityShowcaseRestInterface


object SecurityShowcaseApiProvider {

    val authRestProvider by lazy {
        getRetrofitInterface(SecurityShowcaseRestInterface::class.java)
    }
}

internal fun <T> getRetrofitInterface(apiInterface: Class<T>): T {
    return SecurityShowcaseRetrofitProvider.provideRetrofit().create(apiInterface)
}

