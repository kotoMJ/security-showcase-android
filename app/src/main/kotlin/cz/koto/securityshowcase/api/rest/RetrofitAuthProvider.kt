package cz.koto.securityshowcase.api.rest

import RetrofitProvider
import cz.koto.securityshowcase.ContextProvider
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.SecurityConfig
import cz.koto.securityshowcase.api.rest.interceptor.AuthMarkerInterceptor
import cz.koto.securityshowcase.model.SecurityShowcaseAPIError
import cz.koto.securityshowcase.utility.Logcat
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

open class RetrofitAuthProvider {

	val retrofitAuth: Retrofit by lazy {
		buildRetrofit(30)
	}


	private fun buildRetrofit(timeoutInSecond: Long): Retrofit {
		val builder = Retrofit.Builder()
		builder.baseUrl(SecurityConfig.getRestEndpoint())
		builder.client(buildClient(timeoutInSecond))
		builder.addConverterFactory(createConverterFactory())
		builder.addCallAdapterFactory(createCallAdapterFactory())
		return builder.build()
	}


	private fun buildClient(timeoutInSecond: Long): OkHttpClient {
		val builder = OkHttpClient.Builder()
		builder.connectTimeout(timeoutInSecond, TimeUnit.SECONDS)
		builder.readTimeout(timeoutInSecond, TimeUnit.SECONDS)
		builder.writeTimeout(timeoutInSecond, TimeUnit.SECONDS)
		builder.addInterceptor(AuthMarkerInterceptor())
		builder.addNetworkInterceptor(createLoggingInterceptor())
		return builder.build()
	}


	private fun createLoggingInterceptor(): Interceptor {
		val logger = HttpLoggingInterceptor.Logger { Logcat.d(it) }
		val interceptor = HttpLoggingInterceptor(logger)
		interceptor.level = SecurityConfig.getHttpLoggingLevel()
		return interceptor
	}


	private fun createConverterFactory(): Converter.Factory {
		return GsonConverterFactory.create(RetrofitProvider.gson)
	}


	private fun createCallAdapterFactory(): CallAdapter.Factory {
		return RxJava2CallAdapterFactory.create()
	}

	fun convertRetrofitThrowable(throwable: Throwable): SecurityShowcaseAPIError {
		if (throwable is HttpException) {
			val converter = GsonConverterFactory.create(RetrofitProvider.gson)
					.responseBodyConverter(SecurityShowcaseAPIError::class.java, arrayOfNulls<Annotation>(0),
							retrofitAuth)
			val apiError = converter.convert(throwable.response().errorBody()) as SecurityShowcaseAPIError
			//Use HTTP code until API will have it's own status codes
			apiError.statusCode = throwable.code()
			return apiError
		} else if (throwable is IOException) {
			return SecurityShowcaseAPIError(null, "", ContextProvider.getString(R.string.error_network))
		} else {
			return SecurityShowcaseAPIError(null, "", ContextProvider.getString(R.string.error_unknown))
		}
	}


}