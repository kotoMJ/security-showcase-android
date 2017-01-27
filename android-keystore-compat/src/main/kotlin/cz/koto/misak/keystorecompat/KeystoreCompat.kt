package cz.koto.misak.keystorecompat

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyProperties
import android.util.Log
import cz.koto.misak.keystorecompat.utility.*
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
@TargetApi(Build.VERSION_CODES.KITKAT)
object KeystoreCompat {

    val cipherMode: String = "RSA/None/PKCS1Padding"
    lateinit var context: Context
    lateinit var config: KeystoreCompatConfig

    private val KEYSTORE_KEYWORD = "AndroidKeyStore"
    private lateinit var keyStore: KeyStore
    private lateinit var certSubject: X500Principal
    private lateinit var algorithm: String
    private lateinit var uniqueId: String


    private val LOG_TAG = javaClass.name
    private var encryptedUserString by stringPref("secure_string")
    private var encryptedUserKey by byteArrayPref("secure_key")
    private var lockScreenCancelCount by intPref("sign_up_cancel_count")

    fun <T : KeystoreCompatConfig> overrideConfig(config: T) {
        this.config = config
    }

    /**
     * Keystore is available since API1.
     * CredentialsKeystoreProvider is since API18.
     * Relatively trusted secure keystore is known since API19.
     * Improved security is then since API 23.
     */
    fun isKeystoreCompatAvailable(): Boolean {
        //Pre-KitKat version are not supported
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }

    /**
     * Keystore is available only for secured devices!
     */
    fun isSecurityEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false
        } else {
            return KeystoreCompatImpl.keystoreCompat.isSecurityEnabled(KeystoreCompat.context)
        }
    }


    /**
     * Store byteArray key in encrypted form to shared preferences.
     * Call this function in separated thread, as eventual keyPair init may takes longer time
     */
    fun storeByteArrayKey(byteArrayKey: ByteArray, onError: () -> Unit) {
        runSinceKitKat {
            Log.d(LOG_TAG, "Before load KeyPair...")
            if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
                initKeyPairIfNecessary(uniqueId)
                KeystoreCompat.encryptedUserKey = KeystoreCrypto.encryptKey(byteArrayKey, KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry)
            } else {
                onError.invoke()
            }
        }
    }

    /**
     * Store credentials string in encrypted form to shared preferences.
     * Call this function in separated thread, as eventual keyPair init may takes longer time
     */
    fun storeCredentials(composedCredentials: String, onError: () -> Unit) {
        runSinceKitKat {
            Log.d(LOG_TAG, "Before load KeyPair...")
            if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
                initKeyPairIfNecessary(uniqueId)
                KeystoreCompat.encryptedUserString = KeystoreCrypto.encryptCredentials(composedCredentials, KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.PrivateKeyEntry)
            } else {
                onError.invoke()
            }
        }
    }

    /**
     * Check if shared preferences contains secret credentials to be loadable.
     */
    fun hasCredentialsLoadable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKeystoreCompatAvailable() && isSecurityEnabled()) {//Is usage of Keystore allowed?
                if (lockScreenCancelled()) return false
                return ((encryptedUserString?.isNotBlank() ?: false) //Is there content to decrypt
                        && (keyStore.getEntry(uniqueId, null) != null))//Is there a key for decryption?
            } else return false
        } else return false
    }

    /**
     * Check if shared preferences contains secret key to be loadable.
     */
    fun hasByteArrayKeyLoadable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKeystoreCompatAvailable() && isSecurityEnabled()) {//Is usage of Keystore allowed?
                if (lockScreenCancelled()) return false
                return ((encryptedUserKey.isNotEmpty()) //Is there content to decrypt
                        && (keyStore.getEntry(uniqueId, null) != null))//Is there a key for decryption?
            } else return false
        } else return false
    }

    /**
     * Load byte key in decrypted form from shared preferences.
     */
    fun loadByteArrayKey(onSuccess: (byteArrayKey: ByteArray) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?) {
        runSinceKitKat {
            val privateEntry: KeyStore.PrivateKeyEntry = KeystoreCompat.keyStore.getEntry(KeystoreCompat.uniqueId, null) as KeyStore.PrivateKeyEntry
            KeystoreCompatImpl.keystoreCompat.loadIvAndEncryptedKey(onSuccess,
                    onFailure,
                    { clearCredentials() },
                    forceFlag,
                    this.encryptedUserKey,
                    privateEntry)
        }
    }

    /**
     * Load credentials string in decrypted form from shared preferences
     */
    fun loadCredentials(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?) {
        runSinceKitKat {
            val privateEntry: KeyStore.PrivateKeyEntry = KeystoreCompat.keyStore.getEntry(KeystoreCompat.uniqueId, null) as KeyStore.PrivateKeyEntry
            KeystoreCompatImpl.keystoreCompat.loadCredentials(onSuccess,
                    onFailure,
                    { clearCredentials() },
                    forceFlag,
                    this.encryptedUserString,
                    privateEntry)
        }
    }

    /**
     * CleanUp credentials string from shared preferences.
     */
    fun clearCredentials() {
        runSinceKitKat {
            encryptedUserString = ""
            keyStore.deleteEntry(uniqueId)
            if (keyStore.containsAlias(uniqueId))
                throw RuntimeException("Cert delete wasn't successful!")
        }
    }

    /**
     * Call this for every Android's LockScreen cancellation to be able dismiss
     * KeystoreCompat LockScreen feature after certain amount of users cancellation.
     */
    fun increaseLockScreenCancel() {
        runSinceKitKat {
            lockScreenCancelCount++
        }
    }

    /**
     * Call this for every successful signIn to activate eventually dismissed KeystoreCompat LockScreen feature.
     */
    fun signInSuccessful() {
        runSinceKitKat {
            lockScreenCancelCount = 0
        }
    }

    internal fun init(context: Context) {
        /**
         * Developer note: don't access config object in init!
         * Why? auto-init in KeystoreCompatInitProvider might be initialized before user overrides the config.
         */
        if (!isKeystoreCompatAvailable()) {
            logUnsupportedVersionForKeystore()
        }
        runSinceKitKat {
            this.context = context
            this.uniqueId = Settings.Secure.getString(KeystoreCompat.context.getContentResolver(), Settings.Secure.ANDROID_ID)
            Log.d(LOG_TAG, "uniqueId:${uniqueId}")
            PrefDelegate.initialize(this.context)
            certSubject = X500Principal("CN=$uniqueId, O=Android Authority")

            algorithm = "RSA"
            runSinceMarshmallow {
                algorithm = KeyProperties.KEY_ALGORITHM_RSA
            }
            keyStore = KeyStore.getInstance(KEYSTORE_KEYWORD)
            keyStore.load(null)
            KeystoreCompatImpl.init(Build.VERSION.SDK_INT)
        }
    }

    internal fun initKeyPairIfNecessary(alias: String) {
        if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
            if (keyStore.containsAlias(alias) && isCertificateValid()) return
            else createNewKeyPair(alias)
        }
    }

    internal fun lockScreenCancelled(): Boolean {
        return lockScreenCancelCount >= config.getDialogDismissThreshold()
    }

    private fun logUnsupportedVersionForKeystore() {
        Log.w(LOG_TAG, "Device Android version[${Build.VERSION.SDK_INT}] doesn't offer trusted keystore functionality!")
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
        val generator = KeyPairGenerator.getInstance(algorithm, KEYSTORE_KEYWORD)
        generator.initialize(KeystoreCompatImpl.keystoreCompat.getAlgorithmParameterSpec(this.certSubject, alias, start, end, this.context))
        generator.generateKeyPair()
        if (!keyStore.containsAlias(alias))
            throw RuntimeException("KeyPair was NOT stored!")
    }

}

