package cz.koto.misak.keystorecompat

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Log
import java.math.BigInteger
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

    override fun loadIvAndEncryptedKey(onSuccess: (ByteArray) -> Unit,
                                       onFailure: (Exception) -> Unit,
                                       clearCredentials: () -> Unit,
                                       forceFlag: Boolean?,
                                       ivAndEncryptedKey: ByteArray,
                                       privateKeyEntry: KeyStore.PrivateKeyEntry) {
        try {
            if (forceFlag != null && forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
                //TODO call this in app: forceSignUpLollipop(activity)
                onFailure(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCrypto.decryptKey(privateKeyEntry, ivAndEncryptedKey))
            }

        } catch (e: Exception) {
            //TODO call this in app: forceSignUpLollipop(acrivity)
            onFailure(e)
        }
    }

    override fun loadCredentials(onSuccess: (String) -> Unit,
                                 onFailure: (Exception) -> Unit,
                                 clearCredentials: () -> Unit,
                                 forceFlag: Boolean?,
                                 encryptedUserData: String,
                                 privateKeyEntry: KeyStore.PrivateKeyEntry) {
        try {
            if (forceFlag != null && forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
                //TODO call this in app: forceSignUpLollipop(activity)
                onFailure(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCrypto.decryptCredentials(privateKeyEntry, encryptedUserData))
            }

        } catch (e: Exception) {
            //TODO call this in app: forceSignUpLollipop(acrivity)
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


}