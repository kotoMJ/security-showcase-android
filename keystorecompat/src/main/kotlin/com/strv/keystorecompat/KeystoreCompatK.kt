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
@TargetApi(Build.VERSION_CODES.KITKAT)
internal object KeystoreCompatK : KeystoreCompatFacade {

    val LOG_TAG = javaClass.name

    override fun loadCredentials(onSuccess: (String) -> Unit,
                                 onFailure: (Exception) -> Unit,
                                 clearCredentials: () -> Unit,
                                 forceFlag: Boolean?,
                                 encryptedUserData: String,
                                 privateKeyEntry: KeyStore.PrivateKeyEntry) {
        try {
            SecurityDeviceAdmin.INSTANCE.forceLockPreLollipop(onFailure)
            onSuccess.invoke(KeystoreCrypto.decryptCredentials(privateKeyEntry, encryptedUserData))
        } catch (e: Exception) {

        }
    }

    override fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            throw RuntimeException("Don't use KeyPairGeneratorSpec under Android version KITKAT!")
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
        Log.d(LOG_TAG, "KEYGUARD-SECURE:${km.isKeyguardSecure}")
        Log.d(LOG_TAG, "KEYGUARD-LOCKED:${km.isKeyguardLocked}")
        return km.isKeyguardSecure
    }

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private fun forceSignUpLollipop(activity: AppCompatActivity) {
//        var km: KeyguardManager = KeystoreCompat.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//        val intent = km.createConfirmDeviceCredentialIntent(/*KeystoreCompat.context.getString(R.string.keystore_android_auth_title)*/"TODO TITLE",
//                /*KeystoreCompat.context.getString(R.string.keystore_android_auth_desc)*/"TODO DESC")
//        if (intent != null) {
//            activity.startActivityForResult(intent, FORCE_SIGNUP_REQUEST)
//        }
//    }
}
