package cz.koto.securityshowcase.api

import RetrofitProvider
import com.apollographql.apollo.ApolloClient
import cz.koto.securityshowcase.SecurityConfig
import cz.koto.securityshowcase.api.rest.router.SecurityShowcaseAuthRouter
import cz.koto.securityshowcase.api.rest.router.SecurityShowcaseRouter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


object SecurityShowcaseApiProvider {

	val restRouter by lazy {
		getRetrofitInterface(SecurityShowcaseRouter::class.java)
	}

	val restAuthRouter by lazy {
		getRetrofitInterface(SecurityShowcaseAuthRouter::class.java)
	}

	val gqlRouter by lazy {
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
	return RetrofitProvider.retrofit.create(apiInterface)
}

