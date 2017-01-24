package com.strv.keystorecompat

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import com.strv.keystorecompat.utility.PrefDelegate
import com.strv.keystorecompat.utility.intPref
import com.strv.keystorecompat.utility.runSinceMarshmallow
import com.strv.keystorecompat.utility.stringPref
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
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
object KeystoreCompat {

    /**
     * SECURITY CONFIG
     */
    val KEYSTORE_KEYWORD = "AndroidKeyStore"
    lateinit var keyStore: KeyStore
    lateinit var certSubject: X500Principal
    val cipherMode: String = "RSA/None/PKCS1Padding"
    lateinit var algorithm: String
    val uniqueId: String = Settings.Secure.getString(KeystoreCompat.context.getContentResolver(), Settings.Secure.ANDROID_ID)

    val KEYSTORE_CANCEL_THRESHOLD = 2 //how many cancellation is necessary to forbid this provider

    val LOG_TAG = javaClass.name
    // In memory baypass/way how to force typing Android credentials for LOLLIPOP based generated keyPairs
    var forceTypeCredentials = true
    lateinit var context: Context
    var encryptedUserData by stringPref("secure_pin_data")

    private var signUpCancelCount by intPref("sign_up_cancel_count")

    fun init(context: Context) {
        this.context = context
        PrefDelegate.initialize(this.context)
        certSubject = X500Principal("CN=" + uniqueId + ", O=Android Authority")

        algorithm = "RSA"
        runSinceMarshmallow {
            algorithm = KeyProperties.KEY_ALGORITHM_RSA
        }
        keyStore = KeyStore.getInstance(KEYSTORE_KEYWORD)
        keyStore.load(null)
        if (!isProviderAvailable()) {
            logUnsupportedVersionForKeystore()
        }
    }

    fun clearCredentials() {
        encryptedUserData = ""
        keyStore.deleteEntry(uniqueId)
        if (keyStore.containsAlias(uniqueId))
            throw RuntimeException("Cert delete wasn't successful!")
    }


    fun hasCredentialsLoadable(): Boolean {
        if (isProviderAvailable() && isSecurityEnabled()) {//Is usage of Keystore allowed?
            if (signUpCancelled()) return false
            return ((encryptedUserData?.isNotBlank() ?: false) //Is there content to decrypt
                    && (keyStore.getEntry(uniqueId, null) != null))//Is there a key for decryption?
        } else return false
    }

    fun increaseSignUpCancel() {
        signUpCancelCount++
    }

    fun successSignUp() {
        signUpCancelCount = 0
    }

    fun signUpCancelled(): Boolean {
        return signUpCancelCount >= KEYSTORE_CANCEL_THRESHOLD
    }


    /**
     * Keystore is available since API1. CredentialsKeystoreProvider is since API18.
     * Trusted secure keystore is known since API19.
     * Improved security is then since API 23.
     */
    fun isProviderAvailable(): Boolean {
        //Pre-Lollipop solution must be in different/isolated class (because of run-time exception)!
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    /**
     * Keystore is available only for secured devices!
     */
    fun isSecurityEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return isKeyguardSecuredLollipop()
        } else {
            return isDeviceSecuredMarshmallow()
        }
    }

    /**
     * Call this function in separated thread, as eventual keyPair init may takes longer time
     */
    fun storeCredentials(composedCredentials: String, onError: () -> Unit) {
        Log.d(LOG_TAG, "Before load KeyPair...")
        if (isProviderAvailable() && isSecurityEnabled()) {
            initKeyPairIfNecessary(uniqueId)
            KeystoreCrypto.encryptCredentials(composedCredentials, KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry)
        } else {
            onError.invoke()
        }
    }

    fun loadCredentials(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            loadCredentialsKitKat(activity, onSuccess)
//        } else {
//        }

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            loadCredentialsMarshmallow(onSuccess, onFailure, forceFlag ?: forceTypeCredentials)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadCredentialsLollipop(onSuccess, onFailure, forceFlag ?: forceTypeCredentials)
        }
    }


    private fun logUnsupportedVersionForKeystore() {
        Log.w(LOG_TAG, "Device Android version[%s] doesn't offer trusted keystore functionality!" + Build.VERSION.SDK_INT)
    }

    /**
     * This will not work to be called from CredentialsKeystoreProvider because of attempt to evaluate
     * M-related code on KitKat (will fail on runtime).
     *
     * TODO prepare different KitKat related CredentialsKeystoreProvider to support pre-lollipop (but KitKat only!)
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun loadCredentialsKitKat(onSuccess: (cre: String) -> Unit, onPermanentFailure: () -> Unit) {
        try {
            SecurityDeviceAdmin.INSTANCE.forceLockPreLollipop(onPermanentFailure)
            onSuccess.invoke(KeystoreCrypto.decryptCredentials(KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry))
        } catch (e: Exception) {

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadCredentialsLollipop(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean) {
        try {

            if (forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
                //TODO call this in app: forceSignUpLollipop(activity)
                onFailure(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCrypto.decryptCredentials(KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry))
            }
        } catch (e: Exception) {
            //TODO call this in app: forceSignUpLollipop(acrivity)
            onFailure(e)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun loadCredentialsMarshmallow(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean) {
        try {

            if (forceFlag) {
                //Force signUp by using in memory flag:forceTypeCredentials
                //This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable

                //TODO call this in app: forceSignUpLollipop(activity)
                onFailure.invoke(RuntimeException("Force flag enabled!"))
            } else {
                onSuccess.invoke(KeystoreCrypto.decryptCredentials(KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry))
            }
        } catch (e: UserNotAuthenticatedException) {
            onFailure.invoke(e)//forceSignUpLollipop(activity)//TODO call this in app: forceSignUpLollipop(activity)
        } catch (e: KeyPermanentlyInvalidatedException) {
            Log.w(LOG_TAG, "KeyPermanentlyInvalidatedException: cleanUp credentials for storage!")
            clearCredentials()
            onFailure.invoke(e) //TODO call this in app: activity.start<LoginActivity>()
        }
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

    fun initKeyPairIfNecessary(alias: String) {
        if (isProviderAvailable() && isSecurityEnabled()) {
            if (keyStore.containsAlias(alias) && isCertificateValid()) return
            else createNewKeyPair(alias)
        }
    }

    private fun isCertificateValid(): Boolean {
        //TODO solve real certificate validity
        return true
    }


    private fun createNewKeyPair(aliasText: String) {
        try {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 1)//TODO handle with outdated certificates!
            generateKeyPair(aliasText, start.time, end.time)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Unable to create keys!", e)
            throw e
        }
    }

    private fun generateKeyPair(alias: String, start: Date, end: Date) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            generateKeyPairMarshMellow(alias, start, end, algorithm)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            generateKeyPairKitKat(alias, start, end, algorithm)
        } else throw RuntimeException("Device Android version " + Build.VERSION.SDK_INT + " doesn't offer trusted keystore functionality!")
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun generateKeyPairKitKat(alias: String, startDate: Date, endDate: Date, algorithm: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            throw RuntimeException("Don't use KeyPairGeneratorSpec under Android version KITKAT!")
        }
        val generator = KeyPairGenerator.getInstance(algorithm, KEYSTORE_KEYWORD)
        generator.initialize(KeyPairGeneratorSpec.Builder(KeystoreCompat.context)
                .setAlias(alias)
                .setSubject(certSubject)
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setEncryptionRequired()
                .build())

        generator.generateKeyPair()
        if (!keyStore.containsAlias(alias))
            throw RuntimeException("KeyPair was NOT stored!")
    }

    /**
     * Since Marshmallow set digest and padding mode are required.
     * This is because, following good crypto security practices,
     * AndroidKeyStore now locks down the ways a key can be used (signing vs decryption, digest and padding modes, etc.)
     * to a specified set. If you try to use a key in a way you didn't specify when you created it, it will fail.
     * This failure is actually enforced by the secure hardware, if your device has it,
     * so even if an attacker roots the device the key can still only be used in the defined ways.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKeyPairMarshMellow(alias: String, startDate: Date, endDate: Date, algorithm: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            throw RuntimeException("Don't use generateKeyPairMarshMellow under Android version M!")
        }
        val generator = KeyPairGenerator.getInstance(algorithm, KEYSTORE_KEYWORD)
        generator.initialize(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT.or(KeyProperties.PURPOSE_DECRYPT))
                .setCertificateSubject(certSubject)
                .setKeyValidityStart(startDate)
                .setKeyValidityEnd(endDate)
                .setDigests(KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(10)//User has to type challeng in 10 seconds
                .build())
        generator.generateKeyPair()
        if (!keyStore.containsAlias(alias))
            throw RuntimeException("KeyPair was NOT stored!")
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun isKeyguardSecuredLollipop(): Boolean {
        var km: KeyguardManager = KeystoreCompat.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d(LOG_TAG, "KEYGUARD-SECURE:%s" + km.isKeyguardSecure)
        Log.d(LOG_TAG, "KEYGUARD-LOCKED:%s" + km.isKeyguardLocked)
        return km.isKeyguardSecure
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun isDeviceSecuredMarshmallow(): Boolean {
        var km: KeyguardManager = KeystoreCompat.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        Log.d(LOG_TAG, "DEVICE-SECURE:%s" + km.isDeviceSecure)
        Log.d(LOG_TAG, "DEVICE-LOCKED:%s" + km.isDeviceLocked)
        Log.d(LOG_TAG, "KEYGUARD-SECURE:%s" + km.isKeyguardSecure)
        Log.d(LOG_TAG, "KEYGUARD-LOCKED:%s" + km.isKeyguardLocked)
        return km.isDeviceSecure
    }
}

