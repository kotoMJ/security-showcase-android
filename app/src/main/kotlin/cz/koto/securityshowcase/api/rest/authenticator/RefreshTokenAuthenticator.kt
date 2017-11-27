package cz.koto.securityshowcase.api.rest.authenticator

import cz.koto.securityshowcase.api.rest.interceptor.AUTH_MARKER_REFRESH_HEADER
import cz.koto.securityshowcase.api.rest.router.SecurityShowcaseAuthRouter
import cz.koto.securityshowcase.model.AuthRequestSimple
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.utility.Logcat
import cz.koto.securityshowcase.utility.isValidJWT
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Refresh token authenticator - you can refresh expired auth token based on saved refresh token or credentials
 *
 * https://github.com/square/okhttp/wiki/Recipes
 * Consider also handling 407 Proxy-Authorization: https://square.github.io/okhttp/3.x/okhttp/okhttp3/Authenticator.html
 */
class RefreshTokenAuthenticator constructor(private val api: SecurityShowcaseAuthRouter) : Authenticator {

	override fun authenticate(route: Route, response: Response): Request? {


		if (response.request().header(AUTH_MARKER_REFRESH_HEADER) != null) {
			Logcat.w("refresh was already executed! Returning...")
			return null
		}

		if (response.code() != 401) {
			Logcat.w("RefreshTokenAuthenticator cannot handle response code %d", response.code())
			return null
		}


		// you can eventually parse body of the 401 response and decide if it is requirement for refresh token
		//val bodyString = response.peekBody(Long.MAX_VALUE).string()

		CredentialStorage.getUserName()?.let { email ->
			CredentialStorage.getPassword()?.let { password ->
				val authResponse = api.loginJWT(AuthRequestSimple(email, password)).blockingGet()!!

				if (isValidJWT(authResponse?.idToken)) {
					CredentialStorage.storeUser(authResponse.idToken!!, email, password)

					return response.request().newBuilder()
							.removeHeader("Authorization")
							.addHeader("Authorization", CredentialStorage.getAccessToken()).build()
				}
				Logcat.w("RefreshTokenAuthenticator was unable to refresh sign-in based on saved credentials")
				return null
			} ?:
					run {
						Logcat.w("RefreshTokenAuthenticator was unable to refresh sign-in user because of missing password")
						return null
					}
		} ?: run {
			Logcat.w("RefreshTokenAuthenticator was unable to refresh sign-in user because of missing username")
			return null
		}
	}
}

