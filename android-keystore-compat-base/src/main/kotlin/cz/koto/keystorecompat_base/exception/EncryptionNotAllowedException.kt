package cz.koto.keystorecompat_base.exception

/**
 * In common code (I mean code for all API's 19,21,23,25,...) we cannot use construction available since 23 only.
 * Above described case is also android.security.keystore.UserNotAuthenticatedException
 * We will therefore use this as placeholder for UserNotAuthenticatedException to be signalised also in lower API.
 *
 */
class EncryptionNotAllowedException : KeystoreCompatException {

	var keystoreCompatAvailable: Boolean
	var securityEnabled: Boolean

	constructor(keystoreCompatAvailable: Boolean, securityEnabled: Boolean) {
		this.keystoreCompatAvailable = keystoreCompatAvailable
		this.securityEnabled = securityEnabled
	}
}
