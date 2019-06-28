package cz.kotox.securityshowcase.core.network.rest

import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class HeaderRequestInterceptor @Inject constructor(
	private val preferences: PreferencesCommon
) : Interceptor {

	companion object {
		const val AUTHORIZATION_HEADER = "Authorization"
		const val ACCEPT_CHARSET_HEADER = "Accept-Charset"
		const val CONTENT_TYPE_HEADER = "Content-Type"
	}

	@Throws(IOException::class)
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = buildNewRequest(chain)
		return chain.proceed(request)
	}

	private fun buildNewRequest(chain: Interceptor.Chain): Request {
		val builder = chain.request().newBuilder()

		builder.addHeader(ACCEPT_CHARSET_HEADER, "utf-8")
		builder.addHeader(CONTENT_TYPE_HEADER, "application/json")

		if (preferences.userId != PreferencesCommon.ID_TOKEN_DEFAULT_VALUE) {
			builder.addHeader(AUTHORIZATION_HEADER, "Bearer $preferences.jwtToken")
		}

		return builder.build()
	}
}

//....pridej dalsi restovy tridy RetrofitClient/RetrofitMockclient/ ...
