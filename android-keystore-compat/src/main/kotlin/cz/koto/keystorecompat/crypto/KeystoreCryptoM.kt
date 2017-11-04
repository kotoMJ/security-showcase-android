package cz.koto.keystorecompat.crypto

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyNotYetValidException
import android.security.keystore.UserNotAuthenticatedException
import android.util.Base64
import android.util.Log
import cz.koto.keystorecompat_base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat_base.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat_base.exception.KeystoreInvalidKeyException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.GCMParameterSpec

/**
 * Cryptographic methods API version since Android-M.
 * Don't use these methods on lower API than 23!
 * Don't even place them as non-executable code to code running on lower API (especially KitKat) it would fail at Runtime!
 */
class KeystoreCryptoM(val keystoreCompat: KeystoreCompatFacade) {

	private val LOG_TAG = javaClass.name
	val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN

	@TargetApi(Build.VERSION_CODES.M)
	fun encryptAES(secret: ByteArray, secretKeyEntry: KeyStore.SecretKeyEntry, useBase64Encoding: Boolean): String {
		var iv: ByteArray
		var encryptedKeyForRealm: ByteArray
		try {
			val key = secretKeyEntry.secretKey
			val inCipher = Cipher.getInstance(keystoreCompat.getCipherMode())
			inCipher.init(Cipher.ENCRYPT_MODE, key)
			encryptedKeyForRealm = inCipher.doFinal(secret)
			iv = inCipher.iv

		} catch (nve: KeyNotYetValidException) {
			Log.e(LOG_TAG, "encryptAES error: key's validity start date is probably in the future", nve)
			/**
			 * TODO solve android.security.keystore.KeyNotYetValidException: Key not yet valid
			 * - Indicates that a cryptographic operation failed because the employed key's validity start date is in the future.
			 */
			throw nve
		} catch (nae: UserNotAuthenticatedException) {
			Log.i(LOG_TAG, "User probably exceeded setUserAuthenticationValidityDurationSeconds", nae)
			/**
			 *  TODO solve UserNotAuthenticatedException when user want to encrypt data and user exceeded setUserAuthenticationValidityDurationSeconds
			 * android.security.keystore.UserNotAuthenticatedException: User not authenticated
			 * at android.security.KeyStore.getInvalidKeyException(KeyStore.java:712)
			 * at javax.crypto.Cipher.init(Cipher.java:1143)
			 */
			throw ForceLockScreenMarshmallowException()
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Unexpected encryptAES error", e)
			throw e
		}
		val ivAndEncryptedKey = ByteArray(Integer.SIZE + iv.size + encryptedKeyForRealm.size)

		val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
		buffer.order(ORDER_FOR_ENCRYPTED_DATA)
		buffer.putInt(iv.size)
		buffer.put(iv)
		buffer.put(encryptedKeyForRealm)


		if (useBase64Encoding) {
			return Base64.encodeToString(ivAndEncryptedKey, Base64.DEFAULT)
		} else {
			return String(ivAndEncryptedKey, Charsets.UTF_8)
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	fun decryptAES(secretKeyEntry: KeyStore.SecretKeyEntry, encryptedSecret: String, isBase64Encoded: Boolean): ByteArray {

		var ivAndEncryptedKey: ByteArray = if (isBase64Encoded) Base64.decode(encryptedSecret, Base64.DEFAULT) else encryptedSecret.toByteArray(Charsets.UTF_8)


		val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
		buffer.order(ORDER_FOR_ENCRYPTED_DATA)

		val ivLength = buffer.int
		val iv = ByteArray(ivLength)
		val encryptedKey = ByteArray(ivAndEncryptedKey.size - Integer.SIZE - ivLength)

		buffer.get(iv)
		buffer.get(encryptedKey)

		try {
			val cipher = Cipher.getInstance(keystoreCompat.getCipherMode())
			val ivSpec = GCMParameterSpec(128, iv)
			cipher.init(Cipher.DECRYPT_MODE, secretKeyEntry.secretKey, ivSpec)

			return cipher.doFinal(encryptedKey)

		} catch (e: Exception) {
			when (e) {
				is InvalidKeyException -> {
					throw KeystoreInvalidKeyException()
				}
				is UnrecoverableKeyException -> {
				}
				is NoSuchAlgorithmException -> {
				}
				is BadPaddingException -> {
				}
				is KeyStoreException -> {
				}
				is IllegalBlockSizeException -> {
				}
				is InvalidAlgorithmParameterException -> {
				}
			}
			Log.e(LOG_TAG, "decryptAES error", e)
			throw e
		}
	}

}