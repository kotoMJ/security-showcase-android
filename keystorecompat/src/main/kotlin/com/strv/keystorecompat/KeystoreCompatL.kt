package com.strv.keystorecompat

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
 * The Keystore itself is encrypted using (not only) the user’s own lockScreen pin/password,
 * hence, when the device screen is locked the Keystore is unavailable.
 * Keep this in mind if you have a background service that could need to access your application secrets.
 *
 * With KeyStoreProvider each app can only access to their KeyStore instances or aliases!
 *
 * CredentialsKeystoreProvider is intended to be called with Lollipop&Up versions.
 * Call by KitKat is mysteriously failing on M-specific code (even when it is not called).
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal object KeystoreCompatL : KeystoreCompatFacade {
    val LOG_TAG = javaClass.name

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
            throw RuntimeException("Don't use KeyPairGeneratorSpec under Android version LOLLIPOP!")
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