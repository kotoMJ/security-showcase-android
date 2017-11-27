import com.google.gson.GsonBuilder
import cz.koto.securityshowcase.ContextProvider
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.SecurityConfig
import cz.koto.securityshowcase.api.SecurityShowcaseApiProvider
import cz.koto.securityshowcase.model.AuthRequestSimple
import cz.koto.securityshowcase.model.SecurityShowcaseAPIError
import cz.koto.securityshowcase.model.adapter.GsonUtcDateAdapter
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.utility.ApplicationEvent
import cz.koto.securityshowcase.utility.applicationEvents
import cz.koto.securityshowcase.utility.isValidJWT
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object SecurityShowcaseRetrofitProvider {
	val defaultTimeout = 30000L

	val gson by lazy {
		GsonBuilder()
				.setDateFormat(SecurityConfig.getApiDateFormatUtc())
				.registerTypeAdapter(Date::class.java, GsonUtcDateAdapter())
				.create()
	}

	fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
		level = SecurityConfig.getHttpLoggingLevel()
	}

	fun provideRetrofit(url: String? = SecurityConfig.getRestEndpoint()): Retrofit = Retrofit.Builder()
			.client(provideClientBuilder().build())
			.baseUrl(url)
			.addConverterFactory(createConverterFactory())
			.addCallAdapterFactory(createCallAdapterFactory())
			.build()

	fun provideClientBuilder(clientBuilderBase: OkHttpClient.Builder? = null): OkHttpClient.Builder =
			(clientBuilderBase ?: OkHttpClient.Builder())
					.connectTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
					.readTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
					.addInterceptor { chain ->
						val requestBuilder = chain.request().newBuilder().apply {
							addHeader("Content-Type", "application/json")
							CredentialStorage.getAccessToken()?.let { addHeader("Authorization", it) }
						}
						chain.proceed(requestBuilder.build()).apply {
							when (code()) {
								401 -> {
									CredentialStorage.getUserName()?.let { email ->
										CredentialStorage.getPassword()?.let { password ->
											SecurityShowcaseApiProvider.authRestProvider
													.loginJWT(AuthRequestSimple(email, password))
													.subscribeOn(Schedulers.io())
													.subscribe({ response ->
														if (isValidJWT(response?.idToken)) {
															CredentialStorage.storeUser(response.idToken!!, email, password)
															chain.proceed(this.request().apply {
																requestBuilder
																		.removeHeader("Authorization")
																		.addHeader("Authorization", CredentialStorage.getAccessToken()).build()
															}) // test this behavior of repeating request
														} else
															applicationEvents.onNext(ApplicationEvent.RequestLogin) // did got a good response that is the api case but token was empty
													}, { applicationEvents.onNext(ApplicationEvent.RequestLogin) }) // failed for token, so we go to login again
										} ?: applicationEvents.onNext(ApplicationEvent.RequestLogin) // password missing
									} ?: applicationEvents.onNext(ApplicationEvent.RequestLogin) // username missing
								}
								403 -> applicationEvents.onNext(ApplicationEvent.RequestLogin)
								else -> {
								}
							}
						}
					}
					.addInterceptor(provideLoggingInterceptor())

	fun convertRetrofitThrowable(throwable: Throwable): SecurityShowcaseAPIError {
		if (throwable is HttpException) {
			val converter = GsonConverterFactory.create(gson)
					.responseBodyConverter(SecurityShowcaseAPIError::class.java, arrayOfNulls<Annotation>(0),
							provideRetrofit())
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


	private fun createConverterFactory(): Converter.Factory {
		return GsonConverterFactory.create(gson)
	}


	private fun createCallAdapterFactory(): CallAdapter.Factory {
		return RxJava2CallAdapterFactory.create()
	}
}

