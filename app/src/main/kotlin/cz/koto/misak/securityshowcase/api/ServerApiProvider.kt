package cz.koto.misak.securityshowcase.api

import SecurityShowcaseRetrofitProvider
import com.apollographql.apollo.ApolloClient
import cz.koto.misak.securityshowcase.SecurityConfig
import cz.koto.misak.securityshowcase.api.rest.SecurityShowcaseRestInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object SecurityShowcaseApiProvider {

    val authRestProvider by lazy {
        getRetrofitInterface(SecurityShowcaseRestInterface::class.java)
    }

    val authGqlProvider by lazy {
        ApolloClient.builder()
                .serverUrl(SecurityConfig.getGqlEndpoint())
                .okHttpClient(OkHttpClient.Builder().addInterceptor(provideLoggingInterceptor()).build())
                .build()
    }
}


internal fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
    level = SecurityConfig.getHttpLoggingLevel()
}

internal fun <T> getRetrofitInterface(apiInterface: Class<T>): T {
    return SecurityShowcaseRetrofitProvider.provideRetrofit().create(apiInterface)
}

