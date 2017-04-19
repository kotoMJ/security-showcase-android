package cz.koto.misak.securityshowcase.api.base

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.SecurityConfig
import cz.koto.misak.securityshowcase.api.SecurityShowcaseInterface
import cz.koto.misak.securityshowcase.model.AuthRequestSimple
import cz.koto.misak.securityshowcase.model.SecurityShowcaseAPIError
import cz.koto.misak.securityshowcase.model.adapter.GsonUtcDateAdapter
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.utility.ApplicationEvent
import cz.koto.misak.securityshowcase.utility.applicationEvents
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


object SecurityShowcaseApiProvider {

    val authProvider by lazy {
        getRetrofitInterface(SecurityShowcaseInterface::class.java)
    }
}

internal fun <T> getRetrofitInterface(apiInterface: Class<T>): T {
    return SecurityShocaseRetrofitProvider.provideRetrofit().create(apiInterface)
}

object SecurityShocaseRetrofitProvider {
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

    fun provideRetrofit(url: String? = SecurityConfig.getApiEndpoint()): Retrofit = Retrofit.Builder()
            .client(provideClientBuilder().build())
            .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
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
                                    CredentialStorage.getUserName()?.let { username ->
                                        CredentialStorage.getPassword()?.let { password ->
                                            SecurityShowcaseApiProvider.authProvider
                                                    .loginJWT(AuthRequestSimple(username, password))
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe({ response ->
                                                        if (response?.idToken?.isNotEmpty() ?: false) {
                                                            CredentialStorage.storeUser(response, username, password)
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
}


