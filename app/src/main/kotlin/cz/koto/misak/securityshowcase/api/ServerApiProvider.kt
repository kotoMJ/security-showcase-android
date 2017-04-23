package cz.koto.misak.securityshowcase.api

import SecurityShowcaseRetrofitProvider
import com.apollographql.apollo.ApolloClient
import cz.koto.misak.securityshowcase.api.rest.SecurityShowcaseRestInterface
import okhttp3.OkHttpClient


object SecurityShowcaseApiProvider {

    val authRestProvider by lazy {
        getRetrofitInterface(SecurityShowcaseRestInterface::class.java)
    }

    val authGqlProvider by lazy {
        ApolloClient.builder()
                .serverUrl("https://kotopeky.cz/graphql")
                .okHttpClient(OkHttpClient.Builder().build())
                .build()
    }
}


internal fun <T> getRetrofitInterface(apiInterface: Class<T>): T {
    return SecurityShowcaseRetrofitProvider.provideRetrofit().create(apiInterface)
}

