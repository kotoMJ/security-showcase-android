package cz.koto.keystorecompat.base.compat

import android.content.Context
import java.lang.Exception
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

/**
 * Be careful when using java.security.KeyStore$SecretKeyEntry & java.security.KeyStore$PrivateKeyEntry
 * This interface has to define them generally as java.security.Keystore$Entry
 * Cast to specific implementation in specific KeystoreCompat implementation.
 *
 * Experienced implementation rule of thumb:
 * RSA:java.security.KeyStore$PrivateKeyEntry
 * AES:java.security.KeyStore$SecretKeyEntry
 */
interface KeystoreCompatFacade {

	companion object {
		val KEYSTORE_KEYWORD = "AndroidKeyStore"
	}

	fun getAlgorithm(): String

	fun getCipherMode(): String

	fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String

	fun loadSecret(onSuccess: (cre: ByteArray) -> Unit,
				   onFailure: (e: Exception) -> Unit,
				   clearCredentials: () -> Unit,
				   forceFlag: Boolean?,
				   encryptedUserData: String,
				   keyEntry: KeyStore.Entry,
				   isBase64Encoded: Boolean)

	fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec

	fun isSecurityEnabled(context: Context): Boolean

	fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context)

	fun deactivateRights(context: Context)
}