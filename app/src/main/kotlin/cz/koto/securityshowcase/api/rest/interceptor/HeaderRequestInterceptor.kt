package cz.koto.securityshowcase.api.rest.interceptor

import cz.koto.securityshowcase.storage.CredentialStorage
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


open class HeaderRequestInterceptor : Interceptor {
	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val builder = chain.request().newBuilder()

		builder.addHeader("Accept", "application/json")
		builder.addHeader("Accept-Charset", "utf-8")
		builder.addHeader("Content-Type", "application/json")
		CredentialStorage.getAccessToken()?.let { builder.addHeader("Authorization", it) }

		val request = builder.build()
		return chain.proceed(request)
	}
}