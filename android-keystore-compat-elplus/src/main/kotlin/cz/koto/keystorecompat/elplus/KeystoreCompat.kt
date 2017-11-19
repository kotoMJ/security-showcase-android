package cz.koto.keystorecompat.elplus

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import cz.koto.keystorecompat.base.KeystoreCompatBase
import cz.koto.keystorecompat.base.SingletonHolder
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat.base.exception.EncryptionNotAllowedException
import cz.koto.keystorecompat.base.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.base.exception.KeystoreCompatException
import cz.koto.keystorecompat.base.utility.*
import cz.koto.keystorecompat.elplus.compat.KeystoreCompatConfig
import cz.koto.keystorecompat.elplus.compat.KeystoreCompatImpl
import java.security.KeyStore
import java.security.KeyStoreException
import java.util.*
import javax.security.auth.x500.X500Principal


/**
 * The Keystore itself is encrypted using (not only) the user’s own lockScreen pin/password,
 * hence, when the device screen is locked the Keystore is unavailable.
 * Keep this in mind if you have a background service that could need to access your application secrets.
 *
 * With KeyStoreProvider each app can only access to their KeyStore instances or aliases!
 *
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class KeystoreCompat private constructor(val context: Context, override val config: KeystoreCompatConfig = KeystoreCompatConfig()) : KeystoreCompatBase(config) {

	companion object : SingletonHolder<KeystoreCompat, Context, KeystoreCompatConfig>(::KeystoreCompat)

	lateinit var keystoreCompatImpl: KeystoreCompatImpl

	private lateinit var keyStore: KeyStore
	private lateinit var certSubject: X500Principal
	private lateinit var uniqueId: String

	private val LOG_TAG = javaClass.name
	private var encryptedSecret by stringPref("secure_string")
	private var lockScreenCancelCount by intPref("sign_up_cancel_count")

	init {

		runSinceLollipop {
			this.uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
			Log.d(LOG_TAG, "uniqueId:${uniqueId}")
			PrefDelegate.initialize(this.context)
			certSubject = X500Principal("CN=$uniqueId, O=Android Authority")

			keyStore = KeyStore.getInstance(KeystoreCompatFacade.KEYSTORE_KEYWORD)
			keyStore.load(null)
			keystoreCompatImpl = KeystoreCompatImpl(config).apply { init(Build.VERSION.SDK_INT) }
		}
	}

	override fun isKeystoreCompatAvailable(): Boolean {
		val ret = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) && !isDeviceRooted(context);
		if (!ret) {
			logUnsupportedVersionForKeystore()
		}
		return ret
	}


	/**
	 * Keystore is available only for secured devices!
	 */
	override fun isSecurityEnabled(): Boolean {
		return keystoreCompatImpl.keystoreCompat.isSecurityEnabled(this.context)
	}


	/**
	 * Store credentials string in encrypted form to shared preferences.
	 * Call this function in separated thread, as eventual keyPair init may takes longer time
	 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
	 *
	 * @exception EncryptionNotAllowedException
	 * @exception ForceLockScreenMarshmallowException
	 */
	@JvmOverloads
	fun storeSecret(secret: ByteArray, onError: (e: KeystoreCompatException) -> Unit, onSuccess: () -> Unit, useBase64Encoding: Boolean = true) {
		runSinceKitKat {
			Log.d(LOG_TAG, "Before load KeyPair...")
			if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
				initKeyPairIfNecessary(uniqueId)
				try {
					encryptedSecret = keystoreCompatImpl.keystoreCompat.storeSecret(secret,
							keyStore.getEntry(uniqueId, null) as KeyStore.Entry, useBase64Encoding)
					onSuccess.invoke()
				} catch (fle: ForceLockScreenMarshmallowException) {
					clearCredentials()
					onError.invoke(fle)
				} catch (e: Exception) {
					clearCredentials()
					throw e
				}
			} else {
				onError.invoke(EncryptionNotAllowedException(isKeystoreCompatAvailable(), isSecurityEnabled()))
			}
		}
	}

	/**
	 * Store credentials string in encrypted form to shared preferences.
	 * Call this function in separated thread, as eventual keyPair init may takes longer time
	 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
	 *
	 * @param secret - UTF-8 based non-null string
	 *
	 * @exception EncryptionNotAllowedException
	 * @exception ForceLockScreenMarshmallowException
	 */
	@JvmOverloads
	fun storeSecret(secret: String, onError: (e: KeystoreCompatException) -> Unit, onSuccess: () -> Unit, useBase64Encoding: Boolean = true) {
		runSinceKitKat {
			Log.d(LOG_TAG, "Before load KeyPair...")
			if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
				initKeyPairIfNecessary(uniqueId)
				try {
					encryptedSecret = keystoreCompatImpl.keystoreCompat.storeSecret(secret.toByteArray(Charsets.UTF_8),
							keyStore.getEntry(uniqueId, null) as KeyStore.Entry, useBase64Encoding)
					onSuccess.invoke()
				} catch (fle: ForceLockScreenMarshmallowException) {
					clearCredentials()
					onError.invoke(fle)
				} catch (e: Exception) {
					clearCredentials()
					throw e
				}
			} else {
				clearCredentials()
				onError.invoke(EncryptionNotAllowedException(isKeystoreCompatAvailable(), isSecurityEnabled()))
			}
		}
	}

	/**
	 * Check if shared preferences contains secret credentials to be loadable.
	 */
	fun hasSecretLoadable(): Boolean {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (isKeystoreCompatAvailable() && isSecurityEnabled()) {//Is usage of Keystore allowed?
				if (lockScreenCancelled()) return false
				return ((encryptedSecret?.isNotBlank() ?: false) //Is there content to decrypt
						&& (keyStore.getEntry(uniqueId, null) != null))//Is there a key for decryption?
			} else return false
		} else return false
	}


	/**
	 * Load secret byteArray in decrypted form from shared preferences
	 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
	 */
	@JvmOverloads
	fun loadSecret(onSuccess: (cre: ByteArray) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?, isBase64Encoded: Boolean = true) {
		runSinceLollipop {
			val privateEntry: KeyStore.Entry? = keyStore.getEntry(uniqueId, null)
			if (privateEntry == null) {
				onFailure.invoke(RuntimeException("No entry in keystore available."))
			} else {
				keystoreCompatImpl.keystoreCompat.loadSecret(onSuccess,
						onFailure,
						{ clearCredentials() },
						forceFlag,
						this.encryptedSecret,
						privateEntry, isBase64Encoded)
			}
		}
	}

	/**
	 * Load secret string in decrypted form from shared preferences
	 * Function is using @JvmOverloads to force optional parameters be optional even in java code.
	 */
	@JvmOverloads
	fun loadSecretAsString(onSuccess: (cre: String) -> Unit, onFailure: (e: Exception) -> Unit, forceFlag: Boolean?, isBase64Encoded: Boolean = true) {
		runSinceLollipop {
			val keyEntry: KeyStore.Entry = keyStore.getEntry(uniqueId, null)
			keystoreCompatImpl.keystoreCompat.loadSecret(
					{ byteArray ->
						onSuccess.invoke(String(byteArray, 0, byteArray.size, Charsets.UTF_8))
					},
					onFailure,
					{ clearCredentials() },
					forceFlag,
					this.encryptedSecret,
					keyEntry, isBase64Encoded)
		}
	}

	/**
	 * Deactivate KeystoreCompat usage & make cleanup:
	 * - cleanup credentials string from shared preferences.
	 * - deactivate eventual requested DEVICE_ADMIN righst
	 */
	fun deactivate() {
		clearCredentials()
		keystoreCompatImpl.keystoreCompat.deactivateRights(context)
	}

	/**
	 * CleanUp credentials string from shared preferences.
	 */
	override fun clearCredentials() {
		runSinceLollipop {
			encryptedSecret = ""
			try {
				keyStore.deleteEntry(uniqueId)
			} catch (ke: KeyStoreException) {
				Log.w(LOG_TAG, "Unable to delete entry:" + uniqueId, ke)
			}
		}
	}

	/**
	 * Call this for every Android's LockScreen cancellation to be able dismiss
	 * KeystoreCompat LockScreen feature after certain amount of users cancellation.
	 */
	fun increaseLockScreenCancel() {
		runSinceLollipop {
			lockScreenCancelCount++
		}
	}

	/**
	 * Call this for every successful signIn to activate eventually dismissed KeystoreCompat LockScreen feature.
	 */
	fun lockScreenSuccessful() {
		runSinceLollipop {
			lockScreenCancelCount = 0
		}
	}

	internal fun lockScreenCancelled(): Boolean {
		return lockScreenCancelCount >= config.getDialogDismissThreshold()
	}

	internal fun initKeyPairIfNecessary(alias: String) {
		if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
			if (keyStore.containsAlias(alias) && isCertificateValid()) return
			else createNewKeyPair(alias)
		}
	}


	private fun isCertificateValid(): Boolean = true //TODO solve real certificate validity


	private fun createNewKeyPair(aliasText: String) {
		try {
			val start = Calendar.getInstance()
			start.add(Calendar.MINUTE, -1)//Prevent KeyNotYetValidException for encryption
			val end = Calendar.getInstance()
			end.add(Calendar.YEAR, 1)//TODO handle with outdated certificates!
			keystoreCompatImpl.keystoreCompat.generateKeyPair(aliasText, start.time, end.time, this.certSubject, this.context)
			if (!keyStore.containsAlias(aliasText))
				throw RuntimeException("KeyPair was NOT stored!")
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Unable to create keys!", e)
			throw e
		}
	}

}

