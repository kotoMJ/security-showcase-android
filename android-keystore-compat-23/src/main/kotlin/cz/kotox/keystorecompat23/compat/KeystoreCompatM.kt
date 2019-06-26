package cz.kotox.keystorecompat23.compat

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import cz.kotox.keystorecompat.base.compat.KeystoreCompatFacade
import cz.kotox.keystorecompat.base.exception.KeystoreInvalidKeyException
import cz.kotox.keystorecompat23.crypto.KeystoreCryptoM
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.RSAKeyGenParameterSpec
import java.util.Date
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

/**
 * Marshmallow specific Keystore implementation.
 */
@TargetApi(Build.VERSION_CODES.M)
class KeystoreCompatM(val keystoreCompatConfig: KeystoreCompatConfigM) : KeystoreCompatFacade {

	private val keystoreCryptoM by lazy { KeystoreCryptoM(this) }

	private val LOG_TAG = javaClass.name

	override fun getAlgorithm(): String {
		return KeyProperties.KEY_ALGORITHM_AES
	}

	override fun getCipherMode(): String {
		return "AES/GCM/NoPadding"
	}

	override fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String {
		return keystoreCryptoM.encryptAES(secret, privateKeyEntry as KeyStore.SecretKeyEntry, useBase64Encoding)
	}

	override fun loadSecret(context: Context,
		onSuccess: (cre: ByteArray) -> Unit,
		onFailure: (e: Exception) -> Unit,
		clearCredentials: () -> Unit,
		forceFlag: Boolean?,
		encryptedUserData: String,
		keyEntry: KeyStore.Entry,
		isBase64Encoded: Boolean) {
		try {

			if (forceFlag == null || forceFlag) {
				//Force signUp by using in memory flag:forceTypeCredentials
				//This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
				onFailure.invoke(RuntimeException("Force flag enabled!"))
			} else {
				onSuccess.invoke(keystoreCryptoM.decryptAES(keyEntry as KeyStore.SecretKeyEntry, encryptedUserData, isBase64Encoded))
			}
		} catch (e: UserNotAuthenticatedException) {
			onFailure.invoke(e)
		} catch (e: KeyPermanentlyInvalidatedException) {
			Log.w(LOG_TAG, "KeyPermanentlyInvalidatedException: cleanUp credentials for storage!")
			clearCredentials.invoke()
			onFailure.invoke(e)
		} catch (e: KeystoreInvalidKeyException) {
			Log.w(LOG_TAG, "KeystoreInvalidKeyException: user might dismiss lockScreen.")
			onFailure.invoke(e)
		} catch (e: Exception) {
			onFailure.invoke(e)
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
	@SuppressLint("ObsoleteSdkInt")
	override fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			throw RuntimeException("${LOG_TAG} Unsupported usage of version ${Build.VERSION.SDK_INT}")
		}
		val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT.or(KeyProperties.PURPOSE_DECRYPT))
			.setBlockModes(KeyProperties.BLOCK_MODE_GCM)//follow used getCipherMode
			.setCertificateSubject(certSubject)
			.setKeyValidityStart(startDate)
			.setKeyValidityEnd(endDate)
			.setDigests(KeyProperties.DIGEST_SHA512)
			.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)//follow used getCipherMode
			.setAlgorithmParameterSpec(RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4))//TODO verify this row
			.setUserAuthenticationRequired(keystoreCompatConfig.getUserAuthenticationRequired())
			.setUserAuthenticationValidityDurationSeconds(keystoreCompatConfig.getUserAuthenticationValidityDurationSeconds())
		if (Build.VERSION.SDK_INT > 23) {
			// Generated keys will be invalidated if the biometric templates are added more to user device
			builder.setInvalidatedByBiometricEnrollment(true)
		}
		return builder.build()
	}

	override fun isSecurityEnabled(context: Context): Boolean {
		var km: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		Log.d(LOG_TAG, "DEVICE-SECURE:${km.isDeviceSecure}")
		Log.d(LOG_TAG, "DEVICE-LOCKED:${km.isDeviceLocked}")
		Log.d(LOG_TAG, "KEYGUARD-SECURE:${km.isKeyguardSecure}")
		Log.d(LOG_TAG, "KEYGUARD-LOCKED:${km.isKeyguardLocked}")
		return km.isDeviceSecure
	}

	override fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context) {
		val generator = KeyGenerator.getInstance(getAlgorithm(), KeystoreCompatFacade.KEYSTORE_KEYWORD)
		generator.init(getAlgorithmParameterSpec(certSubject, alias, start, end, context))
		generator.generateKey()
	}

	override fun deactivateRights(context: Context) {
		//Not necessary to implement for M+ variant
	}
}

