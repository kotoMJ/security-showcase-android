package cz.koto.keystorecompat.exception

/**
 * Synonym for UserNotAuthenticatedException
 * Why?
 * In common code (I mean code for all API's 19,21,23,25,...) we cannot use construction available since 23 only.
 * Above described case is also android.security.keystore.UserNotAuthenticatedException
 * We will therefore use this as placeholder for UserNotAuthenticatedException to be signalised also in lower API.
 *
 */
class KeystoreInvalidKeyException : KeystoreCompatException() {

}
