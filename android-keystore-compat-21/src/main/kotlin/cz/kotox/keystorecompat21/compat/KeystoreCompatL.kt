package cz.kotox.keystorecompat21.compat

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Log
import cz.kotox.keystorecompat.base.compat.KeystoreCompatFacade
import cz.kotox.keystorecompat.base.crypto.KeystoreCryptoK
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.Date
import javax.security.auth.x500.X500Principal

/**
 * Lollipop specific Keystore implementation.
 */
@Suppress("MagicNumber")
@TargetApi(21)
open class KeystoreCompatL : KeystoreCompatFacade {
	private val logTag = javaClass.name

	private val keystoreCryptoK by lazy { KeystoreCryptoK(this) }

	override fun getAlgorithm(): String {
		return "RSA"
	}

	override fun getCipherMode(): String {
		return "RSA/None/PKCS1Padding"
	}

	override fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String {
		return keystoreCryptoK.encryptRSA(secret, privateKeyEntry as KeyStore.PrivateKeyEntry, useBase64Encoding)
	}

	override fun loadSecret(context: Context,
		onSuccess: (ByteArray) -> Unit,
		onFailure: (Exception) -> Unit,
		clearCredentials: () -> Unit,
		forceFlag: Boolean?,
		encryptedUserData: String,
		keyEntry: KeyStore.Entry,
		isBase64Encoded: Boolean) {
		try {
			if (forceFlag == null || forceFlag) {
				//Force signUp by using in memory flag:forceTypeCredentials
				//This flag is the same as setUserAuthenticationValidityDurationSeconds(10) [on M version], but using Flag is more stable
				onFailure(RuntimeException("Force flag enabled!"))
			} else {
				onSuccess.invoke(keystoreCryptoK.decryptRSA(keyEntry as KeyStore.PrivateKeyEntry, encryptedUserData, isBase64Encoded))
			}

		} catch (e: Exception) {
			onFailure(e)
		}
	}

	@SuppressLint("ObsoleteSdkInt")
	override fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec {
		@Suppress("MagicNumber")
		if (Build.VERSION.SDK_INT < 21) {    //Just be sure there is no accidental usage below API 21
			throw IllegalAccessException("${logTag} Unsupported usage of version ${Build.VERSION.SDK_INT}")
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
		val km: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		Log.d(logTag, "KEYGUARD-SECURE:%s${km.isKeyguardSecure}")
		Log.d(logTag, "KEYGUARD-LOCKED:%s${km.isKeyguardLocked}")
		return km.isKeyguardSecure
	}

	override fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context) {
		val generator = KeyPairGenerator.getInstance(getAlgorithm(), KeystoreCompatFacade.KEYSTORE_KEYWORD)
		generator.initialize(getAlgorithmParameterSpec(certSubject, alias, start, end, context))
		generator.generateKeyPair()
	}

	override fun deactivateRights(context: Context) {
		//Not necessary to implement for L variant
	}
}