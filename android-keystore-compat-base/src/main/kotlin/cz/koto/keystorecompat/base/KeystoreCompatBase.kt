package cz.koto.keystorecompat.base

import android.content.Context
import android.os.Build
import android.util.Log
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat.base.exception.EncryptionNotAllowedException
import cz.koto.keystorecompat.base.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.base.exception.KeystoreCompatException
import cz.koto.keystorecompat.base.utility.intPref
import cz.koto.keystorecompat.base.utility.runSinceKitKat
import cz.koto.keystorecompat.base.utility.stringPref
import java.security.KeyStore
import java.security.KeyStoreException
import java.util.*
import javax.security.auth.x500.X500Principal


abstract class KeystoreCompatBase(open val config: KeystoreCompatConfigBase, open val context: Context) {

	protected lateinit var keyStore: KeyStore
	protected lateinit var keystoreCompatImpl: KeystoreCompatFacade

	protected lateinit var certSubject: X500Principal
	protected lateinit var uniqueId: String

	protected val LOG_TAG = javaClass.name
	protected var encryptedSecret by stringPref("secure_string")
	protected var lockScreenCancelCount by intPref("sign_up_cancel_count")

	private var isRooted: Boolean? = null

	protected fun isDeviceRooted(context: Context): Boolean {
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

	protected fun logUnsupportedVersionForKeystore() {
		Log.w(LOG_TAG, "Device Android version[${Build.VERSION.SDK_INT}] doesn't offer trusted keystore functionality!")
	}


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
			return keystoreCompatImpl.isSecurityEnabled(this.context)
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
	fun storeSecret(secret: ByteArray, onError: (e: KeystoreCompatException) -> Unit, onSuccess: () -> Unit, useBase64Encoding: Boolean) {
		runSinceKitKat {
			Log.d(LOG_TAG, "Before load KeyPair...")
			if (isKeystoreCompatAvailable() && isSecurityEnabled()) {
				initKeyPairIfNecessary(uniqueId)
				try {
					encryptedSecret = keystoreCompatImpl.storeSecret(secret,
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
					encryptedSecret = keystoreCompatImpl.storeSecret(secret.toByteArray(Charsets.UTF_8),
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
			val privateEntry: KeyStore.Entry? = keyStore.getEntry(uniqueId, null)
			if (privateEntry == null) {
				onFailure.invoke(RuntimeException("No entry in keystore available."))
			} else {
				keystoreCompatImpl.loadSecret(onSuccess,
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
			val keyEntry: KeyStore.Entry = keyStore.getEntry(uniqueId, null)
			keystoreCompatImpl.loadSecret(
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
		keystoreCompatImpl.deactivateRights(context)
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
			keystoreCompatImpl.generateKeyPair(aliasText, start.time, end.time, this.certSubject, this.context)
			if (!keyStore.containsAlias(aliasText))
				throw RuntimeException("KeyPair was NOT stored!")
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Unable to create keys!", e)
			throw e
		}
	}

}
