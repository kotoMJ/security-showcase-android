package cz.koto.keystorecompat.compat

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Log
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.keystorecompat.SecurityDeviceAdmin
import cz.koto.keystorecompat.crypto.KeystoreCryptoK
import cz.koto.keystorecompat.exception.ForceLockScreenKitKatException
import java.math.BigInteger
import java.security.KeyPairGenerator
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

	override fun getAlgorithm(): String {
		return "RSA"
	}

	override fun getCipherMode(): String {
		return "RSA/None/PKCS1Padding"
	}

	override fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String {
		return KeystoreCryptoK.encryptRSA(secret, privateKeyEntry as KeyStore.PrivateKeyEntry, useBase64Encoding)
	}


	override fun loadSecret(onSuccess: (ByteArray) -> Unit,
							onFailure: (Exception) -> Unit,
							clearCredentials: () -> Unit,
							forceFlag: Boolean?,
							encryptedUserData: String,
							keyEntry: KeyStore.Entry,
							isBase64Encoded: Boolean) {
		try {
			SecurityDeviceAdmin.INSTANCE.forceLockPreLollipop(
					{ lockIntent -> onFailure.invoke(ForceLockScreenKitKatException(lockIntent)) },
					{ onSuccess.invoke(KeystoreCryptoK.decryptRSA(keyEntry as KeyStore.PrivateKeyEntry, encryptedUserData, isBase64Encoded)) })
		} catch (e: Exception) {
			onFailure.invoke(e)
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

	override fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context) {
		val generator = KeyPairGenerator.getInstance(KeystoreCompatImpl.keystoreCompat.getAlgorithm(), KeystoreCompat.KEYSTORE_KEYWORD)
		generator.initialize(getAlgorithmParameterSpec(certSubject, alias, start, end, context))
		generator.generateKeyPair()
	}

}
