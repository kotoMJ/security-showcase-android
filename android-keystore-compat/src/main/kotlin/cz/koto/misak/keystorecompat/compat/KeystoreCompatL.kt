package cz.koto.misak.keystorecompat.compat

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Log
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.keystorecompat.crypto.KeystoreCryptoK
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

/**
 * Lollipop specific Keystore implementation.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal object KeystoreCompatL : KeystoreCompatFacade {
    private val LOG_TAG = javaClass.name

    override fun getAlgorithm(): String {
        return "RSA"
    }

    override fun getCipherMode(): String {
        return "RSA/None/PKCS1Padding"
    }

    override fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String {
        return KeystoreCryptoK.encryptRSA(secret, privateKeyEntry as KeyStore.PrivateKeyEntry, useBase64Encoding)
    }

    override fun loadSecret(onSuccess: (ByteArray) -> Unit,
                            onFailure: (Exception) -> Unit,
                            clearCredentials: () -> Unit,
                            forceFlag: Boolean?,
                            encryptedUserData: String,
                            keyEntry: KeyStore.Entry,
                            isBase64Encoded: Boolean) {
        try {
            if (forceFlag == null || forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
                onFailure(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCryptoK.decryptRSA(keyEntry as KeyStore.PrivateKeyEntry, encryptedUserData, isBase64Encoded))
            }

        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            throw RuntimeException("${LOG_TAG} Unsupported usage of version ${Build.VERSION.SDK_INT}")
        }
        return KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(certSubject)
                .setSerialNumber(BigInteger.ONE)//TODO verify this number
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setEncryptionRequired()//This can be source of pain sometimes - generateKeyPair can complain with strange exception
                .build()
    }

    override fun isSecurityEnabled(context: Context): Boolean {
        var km: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d(LOG_TAG, "KEYGUARD-SECURE:%s${km.isKeyguardSecure}")
        Log.d(LOG_TAG, "KEYGUARD-LOCKED:%s${km.isKeyguardLocked}")
        return km.isKeyguardSecure
    }

    override fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context) {
        val generator = KeyPairGenerator.getInstance(KeystoreCompatImpl.keystoreCompat.getAlgorithm(), KeystoreCompat.KEYSTORE_KEYWORD)
        generator.initialize(getAlgorithmParameterSpec(certSubject, alias, start, end, context))
        generator.generateKeyPair()
    }

}