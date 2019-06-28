package cz.kotox.securityshowcase.security.ui

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import cz.kotox.securityshowcase.core.arch.BaseActivity
import timber.log.Timber
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.Executors

abstract class BiometricBaseActivity : BaseActivity() {

	companion object {
		val BIOMETRIC_KEY = "biometric_key"
	}

	lateinit var biometricPrompt: BiometricPrompt

	@Suppress("MagicNumber")
	@RequiresApi(23)
	val observer = Observer<Boolean>() {
		if (it) {
			try {
				initSignature(BIOMETRIC_KEY)
			} catch (unae: UserNotAuthenticatedException) {
				authenticate()
			} catch (th: Throwable) {
				authenticate()
			}
		}
	}

	private fun authenticate() {
		biometricPrompt.authenticate(promptInfo)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		biometricPrompt = createBiometricPrompt()
		appInterface.isAppInForeground.observe(this, observer)
	}

	override fun onDestroy() {
		appInterface.isAppInForeground.removeObserver(observer)
		super.onDestroy()
	}

	protected fun createBiometricPrompt(): BiometricPrompt {
		val executor = Executors.newSingleThreadExecutor()
		val activity: FragmentActivity = this // reference to activity
		val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

			override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
				super.onAuthenticationError(errorCode, errString)

				when (errorCode) {
					BiometricConstants.ERROR_NEGATIVE_BUTTON -> {
						finish()
						appInterface.redirectToLogin()
					}
					BiometricConstants.ERROR_HW_NOT_PRESENT -> {
						Toast.makeText(
							applicationContext,
							"$errString ,TODO fallback authentication",
							Toast.LENGTH_SHORT
						).show()
					}
					BiometricConstants.ERROR_NO_BIOMETRICS -> {
						runOnUiThread {
							Toast.makeText(
								applicationContext,
								"$errString ,No biometrics on device",
								Toast.LENGTH_SHORT
							).show()
						}
					}
					else -> {
						Timber.e("Unhandled errorCode $errorCode")
						finish()
						appInterface.redirectToLogin()
					}

				}
			}

			override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
				super.onAuthenticationSucceeded(result)
				//Dialog disappear in this case, which means fingerprint was recognized. Handle just additional behaviour.
			}

			override fun onAuthenticationFailed() {
				super.onAuthenticationFailed()
				//Simple message to user is handled in dialog, handle just additional behaviour.
				finish()
				appInterface.redirectToLogin()
			}
		})

		return biometricPrompt
	}

	val promptInfo = BiometricPrompt.PromptInfo.Builder()
		.setTitle("Set the title to display.")
		.setSubtitle("Set the subtitle to display.")
		.setDescription("Set the description to display")
		.setNegativeButtonText("Negative Button")
		.build()

	/**
	 * Generate NIST P-256 EC Key pair for signing and verification
	 * @param keyName
	 * @param invalidatedByBiometricEnrollment
	 * @return
	 * @throws Exception
	 */
	@Suppress("MagicNumber")
	@RequiresApi(23)
	@Throws(Exception::class)
	protected fun generateKeyPair(keyName: String, invalidatedByBiometricEnrollment: Boolean): KeyPair {
		val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")

		val builder = KeyGenParameterSpec.Builder(keyName,
			KeyProperties.PURPOSE_SIGN)
			.setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
			.setDigests(KeyProperties.DIGEST_SHA256,
				KeyProperties.DIGEST_SHA384,
				KeyProperties.DIGEST_SHA512)
			// Require the user to authenticate with a biometric to authorize every use of the key
			.setUserAuthenticationRequired(true)
			.setUserAuthenticationValidityDurationSeconds(30)

		@Suppress("MagicNumber")
		if (Build.VERSION.SDK_INT > 23) {
			// Generated keys will be invalidated if the biometric templates are added more to user device
			builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
		}
		keyPairGenerator.initialize(builder.build())


		return keyPairGenerator.generateKeyPair()
	}

	@Throws(Exception::class)
	internal fun getKeyPair(keyName: String): KeyPair? {
		val keyStore = KeyStore.getInstance("AndroidKeyStore")
		keyStore.load(null)
		if (keyStore.containsAlias(keyName)) {
			// Get public key
			val publicKey = keyStore.getCertificate(keyName).publicKey
			// Get private key
			val privateKey = keyStore.getKey(keyName, null) as PrivateKey
			// Return a key pair
			return KeyPair(publicKey, privateKey)
		}
		return null
	}

	@Throws(Exception::class)
	protected fun initSignature(keyName: String): Signature? {
		val keyPair = getKeyPair(keyName)

		if (keyPair != null) {
			val signature = Signature.getInstance("SHA256withECDSA")
			signature.initSign(keyPair.private)
			return signature
		}
		return null
	}
}
