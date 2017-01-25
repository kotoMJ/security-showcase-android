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
 * KitKat specific Keystore implementation.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
internal object KeystoreCompatK : KeystoreCompatFacade {

    private val LOG_TAG = javaClass.name

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
