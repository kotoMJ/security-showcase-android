package cz.koto.misak.keystorecompat

import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

internal object KeystoreCrypto {

    private val LOG_TAG = javaClass.name

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
}