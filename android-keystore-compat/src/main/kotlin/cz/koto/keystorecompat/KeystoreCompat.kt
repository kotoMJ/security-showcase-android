package cz.koto.keystorecompat

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils
import cz.koto.keystorecompat.compat.KeystoreCompatImpl
import cz.koto.keystorecompat.exception.EncryptionNotAllowedException
import cz.koto.keystorecompat.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.exception.KeystoreCompatException
import cz.koto.keystorecompat.utility.PrefDelegate
import cz.koto.keystorecompat.utility.intPref
import cz.koto.keystorecompat.utility.runSinceKitKat
import cz.koto.keystorecompat.utility.stringPref
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
 * CredentialsKeystoreProvider is intended to be called with Lollipop&Up versions.
 * Call by KitKat is mysteriously failing on M-specific code (even when it is not called).
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
object KeystoreCompat {

	lateinit var context: Context
	lateinit var config: KeystoreCompatConfig

	val KEYSTORE_KEYWORD = "AndroidKeyStore"
	private lateinit var keyStore: KeyStore
	private lateinit var certSubject: X500Principal
	private lateinit var uniqueId: String

	private var isRooted: Boolean? = null

	private val LOG_TAG = javaClass.name
	private var encryptedSecret by stringPref("secure_string")
	private var lockScreenCancelCount by intPref("sign_up_cancel_count")

	fun <T : KeystoreCompatConfig> overrideConfig(config: T) {
		this.config = config
	}

	/**
	 * KeystoreCompat is available only for non-rooted devices!
	 * KeystoreCompat is available since API 19 (KitKat)
	 */
	fun isKeystoreCompatAvailable(): Boolean {
		val ret = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && !isDeviceRooted(context);
		if (!ret) {
			logUnsupportedVersionForKeystore()
		}
		return ret
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
					KeystoreCompat.encryptedSecret = KeystoreCompatImpl.keystoreCompat.storeSecret(secret,
							KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.Entry, useBase64Encoding)
					onSuccess.invoke()
				} catch (fle: ForceLockScreenMarshmallowException) {
					KeystoreCompat.clearCredentials()
					onError.invoke(fle)
				} catch (e: Exception) {
					KeystoreCompat.clearCredentials()
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
					KeystoreCompat.encryptedSecret = KeystoreCompatImpl.keystoreCompat.storeSecret(secret.toByteArray(Charsets.UTF_8),
							KeystoreCompat.keyStore.getEntry(uniqueId, null) as KeyStore.Entry, useBase64Encoding)
					onSuccess.invoke()
				} catch (fle: ForceLockScreenMarshmallowException) {
					KeystoreCompat.clearCredentials()
					onError.invoke(fle)
				} catch (e: Exception) {
					KeystoreCompat.clearCredentials()
					throw e
				}
			} else {
				KeystoreCompat.clearCredentials()
				onError.invoke(EncryptionNotAllowedException(isKeystoreCompatAvailable(), isSecurityEnabled()))
			}
		}
	}

	/**
	 * Check if shared preferences contains secret credentials to be loadable.
	 */
	fun hasSecretLoadable(): Boolean {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
		runSinceKitKat {
			val privateEntry: KeyStore.Entry? = KeystoreCompat.keyStore.getEntry(KeystoreCompat.uniqueId, null)
			if (privateEntry == null) {
				onFailure.invoke(RuntimeException("No entry in keystore available."))
			} else {
				KeystoreCompatImpl.keystoreCompat.loadSecret(onSuccess,
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
		runSinceKitKat {
			val keyEntry: KeyStore.Entry = KeystoreCompat.keyStore.getEntry(KeystoreCompat.uniqueId, null)
			KeystoreCompatImpl.keystoreCompat.loadSecret(
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
	 * CleanUp credentials string from shared preferences.
	 */
	fun clearCredentials() {
		runSinceKitKat {
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
		runSinceKitKat {
			lockScreenCancelCount++
		}
	}

	/**
	 * Call this for every successful signIn to activate eventually dismissed KeystoreCompat LockScreen feature.
	 */
	fun lockScreenSuccessful() {
		runSinceKitKat {
			lockScreenCancelCount = 0
		}
	}

	internal fun lockScreenCancelled(): Boolean {
		return lockScreenCancelCount >= config.getDialogDismissThreshold()
	}

	internal fun init(context: Context) {


		/**
		 * Developer note:
		 * - don't access config object in init!
		 * - it means dont't call isDeviceRooted() or isKeystoreCompatAvailable() in init()
		 * Why? auto-init in KeystoreCompatInitProvider might be initialized before user overrides the config.
		 */

		runSinceKitKat {
			this.context = context
			this.config = KeystoreCompatConfig() // default config can be overriden externally later!

			this.uniqueId = Settings.Secure.getString(KeystoreCompat.context.getContentResolver(), Settings.Secure.ANDROID_ID)
			Log.d(LOG_TAG, "uniqueId:${uniqueId}")
			PrefDelegate.initialize(this.context)
			certSubject = X500Principal("CN=$uniqueId, O=Android Authority")


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
			start.add(Calendar.MINUTE, -1)//Prevent KeyNotYetValidException for encryption
			val end = Calendar.getInstance()
			end.add(Calendar.YEAR, 1)//TODO handle with outdated certificates!
			KeystoreCompatImpl.keystoreCompat.generateKeyPair(aliasText, start.time, end.time, this.certSubject, this.context)
			if (!keyStore.containsAlias(aliasText))
				throw RuntimeException("KeyPair was NOT stored!")
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Unable to create keys!", e)
			throw e
		}
	}

	private fun isDeviceRooted(context: Context): Boolean {
		val ret = RootBeer(context).isRooted

		if (this.isRooted == null) {
			if (ret) {
				val check: RootBeer = RootBeer(context)
				Log.w(LOG_TAG, "RootDetection enabled ${config.isRootDetectionEnabled()}")
				Log.w(LOG_TAG, "Root Management Apps ${if (check.detectRootManagementApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "PotentiallyDangerousApps ${if (check.detectPotentiallyDangerousApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "TestKeys ${if (check.detectTestKeys()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "BusyBoxBinary ${if (check.checkForBusyBoxBinary()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "SU Binary ${if (check.checkForSuBinary()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "2nd SU Binary check ${if (check.checkSuExists()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "ForRWPaths ${if (check.checkForRWPaths()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "DangerousProps ${if (check.checkForDangerousProps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "Root via native check ${if (check.checkForRootNative()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "RootCloakingApps ${if (check.detectRootCloakingApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "Selinux Flag Is Enabled ${if (Utils.isSelinuxFlagInEnabled()) "true" else "false"}")
			}
			this.isRooted = ret && config.isRootDetectionEnabled()
		}

		if (this.isRooted!!) clearCredentials()

		return this.isRooted!!
	}

}

