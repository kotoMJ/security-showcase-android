package cz.koto.misak.securityshowcase


import okhttp3.logging.HttpLoggingInterceptor

/**
 * Keep project definitions available on one place.
 */
abstract class SecurityBaseConfig {

    fun getHttpLoggingLevel(): HttpLoggingInterceptor.Level {
        return if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }


    fun getApiDateFormatUtc(): String {
        return "yyyy-MM-dd'T'HH:mm:ss"
    }


    fun isPackageRelease(): Boolean {
        return BuildConfig.BUILD_TYPE == "release"
    }

    fun isPackageDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }

    fun isEndpointDev(): Boolean {
        return true
    }

    fun getTestUsername(): String {
        return "SecurityShowcaseUser"
    }

    fun getTestPass(): String {
        return "passW0rd1234"
    }

}
