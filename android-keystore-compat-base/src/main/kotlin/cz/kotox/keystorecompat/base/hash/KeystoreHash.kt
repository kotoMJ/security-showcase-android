package cz.kotox.keystorecompat.base.hash

import android.util.Log
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Notes about hashing:
 * https://crackstation.net/hashing-security.htm
 * https://www.owasp.org/index.php/Hashing_Java
 *
 * Discussion for above mentioned source:
 * http://security.stackexchange.com/questions/91447/understanding-salted-password-hashing-using-java
 *
 * Good source for hashing sources:
 * https://github.com/defuse/password-hashing/blob/master/PasswordStorage.java
 */

val PBKDF2WithHmacSHA512: String = "PBKDF2WithHmacSHA512"
val PBKDF2WithHmacSHA1: String = "PBKDF2WithHmacSHA1"

val LENGTH32BYTES = 256
val LENGTH64BYTES = 512

inline fun createRandomHashKey(): ByteArray {
	val KEY_LENGTH = 64
	val key = ByteArray(KEY_LENGTH)
	val rng = SecureRandom()
	rng.nextBytes(key)
	return key
}

/**
 * Safe way to hash password based on: https://www.owasp.org/index.php/Hashing_Java
 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
 * @param basePassword
 * @param salt
 * @param iterationCount
 * @param sha512 - If sha512 (PBKDF2WithHmacSHA512) set to true generates a NoSuchAlgorithmException,
 * set sha512 to false (PBKDF2WithHmacSHA1).
 * Both are adequate to the task but you may be criticized when people see "SHA1"
 * in the specification (SHA1 can be unsafe outside of the context of PBKDF2).
 * @param keyLengthInBit - define max/preferred bits of the key (256bits means 32bytes output),look at LENGTH32BYTES,LENGTH64BYTES constants.
 */
@JvmOverloads
inline fun createHashKey(
	basePassword: String,
	salt: ByteArray,
	iterationCount: Int,
	sha512: Boolean,
	keyLengthInBit: Int = LENGTH32BYTES
): ByteArray {
	try {
		val skf = SecretKeyFactory.getInstance(if (sha512) PBKDF2WithHmacSHA512 else PBKDF2WithHmacSHA1)
		val spec = PBEKeySpec(basePassword.toCharArray(), salt, iterationCount, keyLengthInBit)//65536, 256
		val key = skf.generateSecret(spec)
		return key.encoded
	} catch (e: Exception) {
		Log.e("KeystoreHash", "Unable to create hash key!", e)
		throw e
	}
}

/**
 * Simplified and less secure way to hash password (salt and iterationCount are derived from basePassword).
 * Use createHashKey with salt and iterationCount instead, if possible.
 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
 *
 * @param basePassword
 * @param sha512 - If sha512 (PBKDF2WithHmacSHA512) set to true generates a NoSuchAlgorithmException,
 * set sha512 to false (PBKDF2WithHmacSHA1).
 * Both are adequate to the task but you may be criticized when people see "SHA1"
 * in the specification (SHA1 can be unsafe outside of the context of PBKDF2).
 * @param keyLengthInBit - define max/preferred bits of the key (256bits means 32bytes output), look at LENGTH32BYTES,LENGTH64BYTES constants.
 */
@JvmOverloads
inline fun createHashKey(
	basePassword: String,
	sha512: Boolean,
	keyLengthInBit: Int = LENGTH32BYTES
): ByteArray {
	return createHashKey(
		basePassword,
		basePassword.toByteArray(Charset.forName("UTF-32")),
		basePassword.length,
		sha512,
		keyLengthInBit
	)
}
