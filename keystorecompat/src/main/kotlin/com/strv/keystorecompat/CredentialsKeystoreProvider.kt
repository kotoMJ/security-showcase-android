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
import android.util.Base64
import android.util.Log
import com.strv.keystorecompat.KeystoreCompat.encryptedUserData
import com.strv.keystorecompat.utility.intPref
import com.strv.keystorecompat.utility.runSinceMarshmallow
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
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
object CredentialsKeystoreProvider {

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


    private var signUpCancelCount by intPref("sign_up_cancel_count")

    fun init() {
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
            encryptCredentials(composedCredentials, uniqueId)
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
            onSuccess.invoke(decryptCredentials(uniqueId))
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
                onSuccess.invoke(decryptCredentials(uniqueId))
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
                onSuccess.invoke(decryptCredentials(uniqueId))
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

    private fun encryptCredentials(composedCredentials: String, alias: String) {
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey as RSAPublicKey

            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val inCipher = Cipher.getInstance(cipherMode/*, "AndroidOpenSSL"*/)
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, inCipher)
            cipherOutputStream.write(composedCredentials.toByteArray(Charsets.UTF_8))
            cipherOutputStream.close()

            encryptedUserData = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e(LOG_TAG, /*ContextProvider.getString(R.string.keystore_label_encryption_error)*/"Encryption error", e)
            throw e
        }
    }


    private fun decryptCredentials(alias: String): String {
        try {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry

            /**
             * AndroidOpenSSL works on Lollipop.
             * But on marshmallow it throws: java.security.InvalidKeyException: Need RSA private or public key
             *
             * On Android 6.0 you should Not use "AndroidOpenSSL" for cipher creation,
             * it would fail with "Need RSA private or public key" at cipher init for decryption.
             * Simply use Cipher.getInstance("RSA/ECB/PKCS1Padding")
             */
            val output = Cipher.getInstance(cipherMode/*, "AndroidOpenSSL"*/)
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
            Log.d(LOG_TAG, "Credentials encrypted as [%s]" + ret)
            return ret

        } catch (e: Exception) {
            Log.e(LOG_TAG, /*ContextProvider.getString(R.string.keystore_label_decryption_error)*/"decryption error", e)
            throw e
        }
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

