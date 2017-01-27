package cz.koto.misak.keystorecompat

import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

internal object KeystoreCrypto {

    private val LOG_TAG = javaClass.name
    val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN

    fun encryptKey(key: ByteArray, privateKeyEntry: KeyStore.PrivateKeyEntry): ByteArray {
        var iv: ByteArray
        var encryptedKeyForRealm: ByteArray
        try {
            val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val inCipher = Cipher.getInstance(KeystoreCompat.cipherMode/*, "AndroidOpenSSL"*/)
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)

            encryptedKeyForRealm = inCipher.doFinal(key)
            iv = inCipher.iv


        } catch (e: Exception) {
            Log.e(LOG_TAG, "Encryption2 error", e)
            throw e
        }
        val ivAndEncryptedKey = ByteArray(Integer.SIZE + iv.size + encryptedKeyForRealm.size)

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)
        buffer.putInt(iv.size)
        buffer.put(iv)
        buffer.put(encryptedKeyForRealm)

        //val outputStream = ByteArrayOutputStream()
        //val cipherOutputStream = CipherOutputStream(outputStream, inCipher)
        //cipherOutputStream.write(encryptedKeyForRealm)
        //cipherOutputStream.close()

        return ivAndEncryptedKey
    }

    fun decryptKey(privateKeyEntry: KeyStore.PrivateKeyEntry, ivAndEncryptedKey: ByteArray): ByteArray {

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)

        val ivLength = buffer.int
        val iv = ByteArray(ivLength)
        val encryptedKey = ByteArray(ivAndEncryptedKey.size - Integer.SIZE - ivLength)

        buffer.get(iv)
        buffer.get(encryptedKey)

        try {
            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val cipher = Cipher.getInstance(KeystoreCompat.cipherMode/*, "AndroidOpenSSL"*/)
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey, ivSpec)

            return cipher.doFinal(encryptedKey)

        } catch (e: Exception) {
            when (e) {
                is InvalidKeyException -> {
                    throw RuntimeException("key is invalid.")
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
            throw e
        }
    }


    fun encryptCredentials(composedCredentials: String, privateKeyEntry: KeyStore.PrivateKeyEntry): String {
        try {
            val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val inCipher = Cipher.getInstance(KeystoreCompat.cipherMode/*, "AndroidOpenSSL"*/)
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, inCipher)
            cipherOutputStream.write(composedCredentials.toByteArray(Charsets.UTF_8))
            cipherOutputStream.close()

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Encryption error", e)
            throw e
        }
    }

    fun decryptCredentials(privateKeyEntry: KeyStore.PrivateKeyEntry, encryptedUserData: String): String {
        try {

            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val output = Cipher.getInstance(KeystoreCompat.cipherMode/*, "AndroidOpenSSL"*/)
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

            val cipherInputStream = CipherInputStream(
                    ByteArrayInputStream(Base64.decode(encryptedUserData, Base64.DEFAULT)), output)
            val values = ArrayList<Byte>()
            var nextByte: Int = -1

            while ({ nextByte = cipherInputStream.read(); nextByte }() != -1) {
                values.add(nextByte.toByte())
            }

            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            val ret = String(bytes, 0, bytes.size, Charsets.UTF_8)
            Log.d(LOG_TAG, "Credentials encrypted as $ret")
            return ret

        } catch (e: Exception) {
            Log.e(LOG_TAG, "decryption error", e)
            throw e
        }
    }

    fun createRandomHashKey(): ByteArray {
        val KEY_LENGTH = 64
        val key = ByteArray(KEY_LENGTH)
        val rng = SecureRandom()
        rng.nextBytes(key)
        return key
    }

    /**
     * Safe way to hash password based on: https://www.owasp.org/index.php/Hashing_Java
     */
    fun createHashKey(basePassword: String, salt: ByteArray, iterationCount: Int): ByteArray {
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
            val spec = PBEKeySpec(basePassword.toCharArray(), salt, iterationCount, 256)
            val key = skf.generateSecret(spec)
            return key.encoded
        } catch (e: Exception) {
            Log.e(LOG_TAG, "", e)
            throw e
        }
    }
}