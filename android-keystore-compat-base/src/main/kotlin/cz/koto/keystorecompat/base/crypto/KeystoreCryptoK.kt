package cz.koto.keystorecompat.base.crypto

import android.annotation.TargetApi
import android.os.Build
import android.util.Base64
import android.util.Log
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream


/**
 * Cryptographic methods for pre-M Android version (but the minimum SDK is Android KitKat)
 */
class KeystoreCryptoK(val keystoreCompat: KeystoreCompatFacade) {

	private val LOG_TAG = javaClass.name

	/**
	 * Encrypt bytes to Base64 encoded string.
	 * For input secret as string use: secret.toByteArray(Charsets.UTF_8)
	 */

	@TargetApi(Build.VERSION_CODES.KITKAT)
	fun encryptRSA(secret: ByteArray, privateKeyEntry: KeyStore.PrivateKeyEntry, useBase64Encoding: Boolean): String {
		try {
			//When you are using asymmetric encryption algorithms, you need to use the public key to encrypt
			val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

			/**
			 * AndroidOpenSSL works on Lollipop.
			 * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
			 *
			 * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
			 * it would fail with "Need RSA private or public key" at cipher init for decryption.
			 * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
			 */
			val inCipher = Cipher.getInstance(keystoreCompat.getCipherMode()/*, "AndroidOpenSSL"*/)
			inCipher.init(Cipher.ENCRYPT_MODE, publicKey)
			val outputStream = ByteArrayOutputStream()
			val cipherOutputStream = CipherOutputStream(outputStream, inCipher)
			cipherOutputStream.write(secret)
			cipherOutputStream.close()

			if (useBase64Encoding) {
				return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
			} else {
				return String(outputStream.toByteArray(), Charsets.UTF_8)
			}
		} catch (e: Exception) {
			Log.e(LOG_TAG, "encryptRSA error", e)
			throw e
		}
	}

	/**
	 * Decrypt Base64 encoded encrypted byteArray.
	 * For output as string user: String(byteArray, 0, byteArray.size, Charsets.UTF_8)
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	fun decryptRSA(privateKeyEntry: KeyStore.PrivateKeyEntry, encryptedSecret: String, isBase64Encoded: Boolean): ByteArray {
		try {

			var inputByteArray: ByteArray = if (isBase64Encoded) Base64.decode(encryptedSecret, Base64.DEFAULT) else encryptedSecret.toByteArray(Charsets.UTF_8)
			/**
			 * AndroidOpenSSL works on Lollipop.
			 * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
			 *
			 * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
			 * it would fail with "Need RSA private or public key" at cipher init for decryption.
			 * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
			 */
			val output = Cipher.getInstance(keystoreCompat.getCipherMode()/*, "AndroidOpenSSL"*/)
			output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

			val cipherInputStream = CipherInputStream(ByteArrayInputStream(inputByteArray), output)
			val values = ArrayList<Byte>()
			var nextByte: Int = -1

			while ({ nextByte = cipherInputStream.read(); nextByte }() != -1) {
				values.add(nextByte.toByte())
			}

			val bytes = ByteArray(values.size)
			for (i in bytes.indices) {
				bytes[i] = values[i]
			}

			return bytes

		} catch (e: Exception) {
			Log.e(LOG_TAG, "decryptRSA error", e)
			throw e
		}
	}
}