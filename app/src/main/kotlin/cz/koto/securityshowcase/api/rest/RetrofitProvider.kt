import com.google.gson.GsonBuilder
import cz.koto.securityshowcase.SecurityConfig
import cz.koto.securityshowcase.api.rest.RetrofitAuthProvider
import cz.koto.securityshowcase.api.rest.authenticator.RefreshTokenAuthenticator
import cz.koto.securityshowcase.api.rest.interceptor.HeaderRequestInterceptor
import cz.koto.securityshowcase.api.rest.router.SecurityShowcaseAuthRouter
import cz.koto.securityshowcase.model.adapter.GsonUtcDateAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitProvider {

	val retrofit: Retrofit by lazy {
		provideRetrofit(30)
	}


	val gson by lazy {
		GsonBuilder()
				.setDateFormat(SecurityConfig.getApiDateFormatUtc())
				.registerTypeAdapter(Date::class.java, GsonUtcDateAdapter())
				.create()
	}

	val refreshTokenAuthenticator by lazy {
		RefreshTokenAuthenticator(RetrofitAuthProvider().retrofitAuth.create(SecurityShowcaseAuthRouter::class.java))
	}

	val headerRequestInterceptor by lazy {
		HeaderRequestInterceptor()
	}

	fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
		level = SecurityConfig.getHttpLoggingLevel()
	}


	private fun provideRetrofit(timeoutInSecond: Long, url: String? = SecurityConfig.getRestEndpoint()): Retrofit {
		val builder = Retrofit.Builder()
		builder.baseUrl(url)
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
		builder.authenticator(refreshTokenAuthenticator)
		builder.addInterceptor(headerRequestInterceptor)
		builder.addNetworkInterceptor(provideLoggingInterceptor())
		return builder.build()
	}

//	fun provideClientBuilder(clientBuilderBase: OkHttpClient = null): OkHttpClient =
//			(clientBuilderBase ?: OkHttpClient.Builder())
//					.connectTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
//					.readTimeout(defaultTimeout, TimeUnit.MILLISECONDS)
//					.addInterceptor { chain ->
//						val requestBuilder = chain.request().newBuilder().apply {
//							addHeader("Content-Type", "application/json")
//							CredentialStorage.getAccessToken()?.let { addHeader("Authorization", it) }
//						}
//						chain.proceed(requestBuilder.build()).apply {
//							when (code()) {
//								401 -> {
//									CredentialStorage.getUserName()?.let { email ->
//										CredentialStorage.getPassword()?.let { password ->
//											SecurityShowcaseApiProvider.authRestProvider
//													.loginJWT(AuthRequestSimple(email, password))
//													.subscribeOn(Schedulers.io())
//													.subscribe({ response ->
//														if (isValidJWT(response?.idToken)) {
//															CredentialStorage.storeUser(response.idToken!!, email, password)
//															chain.proceed(this.request().apply {
//																requestBuilder
//																		.removeHeader("Authorization")
//																		.addHeader("Authorization", CredentialStorage.getAccessToken()).build()
//															}) // test this behavior of repeating request
//														} else
//															applicationEvents.onNext(ApplicationEvent.RequestLogin) // did got a good response that is the api case but token was empty
//													}, { applicationEvents.onNext(ApplicationEvent.RequestLogin) }) // failed for token, so we go to login again
//										} ?: applicationEvents.onNext(ApplicationEvent.RequestLogin) // password missing
//									} ?: applicationEvents.onNext(ApplicationEvent.RequestLogin) // username missing
//								}
//								403 -> applicationEvents.onNext(ApplicationEvent.RequestLogin)
//								else -> {
//								}
//							}
//						}
//					}
//					.addInterceptor(provideLoggingInterceptor())


	private fun createConverterFactory(): Converter.Factory {
		return GsonConverterFactory.create(gson)
	}


	private fun createCallAdapterFactory(): CallAdapter.Factory {
		return RxJava2CallAdapterFactory.create()
	}
}

