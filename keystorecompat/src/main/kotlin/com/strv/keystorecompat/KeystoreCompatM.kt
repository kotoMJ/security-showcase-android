package com.strv.keystorecompat

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.RSAKeyGenParameterSpec
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
@TargetApi(Build.VERSION_CODES.M)
internal object KeystoreCompatM : KeystoreCompatFacade {

    val LOG_TAG = javaClass.name


    override fun loadCredentials(onSuccess: (cre: String) -> Unit,
                                 onFailure: (e: Exception) -> Unit,
                                 clearCredentials: () -> Unit,
                                 forceFlag: Boolean?,
                                 encryptedUserData: String,
                                 privateKeyEntry: KeyStore.PrivateKeyEntry) {
        try {

            if (forceFlag != null && forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable

                //TODO call this in app: forceSignUpLollipop(activity)
                onFailure.invoke(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCrypto.decryptCredentials(privateKeyEntry, encryptedUserData))
            }
        } catch (e: UserNotAuthenticatedException) {
            onFailure.invoke(e)//forceSignUpLollipop(activity)//TODO call this in app: forceSignUpLollipop(activity)
        } catch (e: KeyPermanentlyInvalidatedException) {
            Log.w(LOG_TAG, "KeyPermanentlyInvalidatedException: cleanUp credentials for storage!")
            clearCredentials.invoke()
            onFailure.invoke(e) //TODO call this in app: activity.start<LoginActivity>()
        }
    }

    /**
     * Since Marshmallow set digest and padding mode are required.
     * This is because, following good crypto security practices,
     * AndroidKeyStore now locks down the ways a key can be used (signing vs decryption, digest and padding modes, etc.)
     * to a specified set. If you try to use a key in a way you didn't specify when you created it, it will fail.
     * This failure is actually enforced by the secure hardware, if your device has it,
     * so even if an attacker roots the device the key can still only be used in the defined ways.
     */
    override fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            throw RuntimeException("Don't use generateKeyPairMarshMellow under Android version M!")
        }
        return KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT.or(KeyProperties.PURPOSE_DECRYPT))
                .setCertificateSubject(certSubject)
                .setKeyValidityStart(startDate)
                .setKeyValidityEnd(endDate)
                .setDigests(KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4))//TODO verify this row
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(10)//User has to type challenge in 10 seconds
                .build()
    }

    override fun isSecurityEnabled(context: Context): Boolean {
        var km: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d(LOG_TAG, "DEVICE-SECURE:${km.isDeviceSecure}")
        Log.d(LOG_TAG, "DEVICE-LOCKED:${km.isDeviceLocked}")
        Log.d(LOG_TAG, "KEYGUARD-SECURE:${km.isKeyguardSecure}")
        Log.d(LOG_TAG, "KEYGUARD-LOCKED:${km.isKeyguardLocked}")
        return km.isDeviceSecure
    }
}

