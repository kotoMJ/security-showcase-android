package cz.koto.misak.keystorecompat

import android.util.Log
import java.nio.charset.Charset
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object KeystoreHash {

    inline fun createRandomHashKey(): ByteArray {
        val KEY_LENGTH = 64
        val key = ByteArray(KEY_LENGTH)
        val rng = SecureRandom()
        rng.nextBytes(key)
        return key
    }

    /**
     * Safe way to hash password based on: https://www.owasp.org/index.php/Hashing_Java
     */
    inline fun createHashKey(basePassword: String, salt: ByteArray, iterationCount: Int): ByteArray {
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
            val spec = PBEKeySpec(basePassword.toCharArray(), salt, iterationCount, 256)
            val key = skf.generateSecret(spec)
            return key.encoded
        } catch (e: Exception) {
            Log.e(javaClass.name, "", e)
            throw e
        }
    }

    /**
     * Simplified and less secure way to hash password.
     * Use createHashKey with salt and iterationCount instead, if possible.
     */
    inline fun createHashKey(basePassword: String): ByteArray {
        return createHashKey(basePassword, basePassword.toByteArray(Charset.forName("UTF-32")), basePassword.length)
    }
}