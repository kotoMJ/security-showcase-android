package cz.koto.securityshowcase.utility

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT


inline fun isValidJWT(token: String?, checkExpiration: Boolean = false): Boolean {
	if (token == null) return false
	try {
		val jwt = JWT(token)
		if (checkExpiration) {
			val expired = jwt.isExpired(10)
			Logcat.w("JWT isExpired: %s", expired)
			if (expired) return false
		}
	} catch (de: DecodeException) {
		Logcat.e(de, "JWT decodeException!")
		return false
	} catch (e: Exception) {
		Logcat.e(e, "JWT unexpectedException!")
		return false
	}
	return true
}