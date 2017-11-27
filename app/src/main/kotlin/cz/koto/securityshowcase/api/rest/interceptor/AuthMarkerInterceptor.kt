package cz.koto.securityshowcase.api.rest.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

const val AUTH_MARKER_REFRESH_HEADER = "RefreshAttempt"

open class AuthMarkerInterceptor : Interceptor {
	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val builder = chain.request().newBuilder()
		builder.addHeader("Accept", "application/json")
		builder.addHeader("Accept-Charset", "utf-8")
		builder.addHeader("Content-Type", "application/json")
		builder.addHeader(AUTH_MARKER_REFRESH_HEADER, "true")
		val request = builder.build()
		return chain.proceed(request)
	}
}