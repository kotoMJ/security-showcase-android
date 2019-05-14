package cz.kotox.securityshowcase.core.arch

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import cz.kotox.securityshowcase.core.AppInterface
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.Executors
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, BaseUIScreen {

	companion object {
		val BIOMETRIC_KEY = "biometric_key"
	}

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

	@Inject
	lateinit var appInterface: AppInterface

	lateinit var biometricPrompt: BiometricPrompt

	//TODO not ideal to have abstract method, ... might be improved.
	abstract fun isCoveredByBiometric(): Boolean

	@RequiresApi(Build.VERSION_CODES.M)
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

	override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
		return dispatchingAndroidInjector
	}

	override val baseActivity: BaseActivity get() = this
	override var lastSnackbar: Snackbar? = null

	public override fun onCreate(savedInstanceState: Bundle?) {
		//CrashlyticsUtility.setCurrentActivityKey(javaClass.simpleName)
		Timber.v(javaClass.simpleName)
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true) //VectorDrawables visible on KitKat

		if (isCoveredByBiometric()) {
			biometricPrompt = createBiometricPrompt()
			appInterface.isAppInForeground.observe(this, observer)
		}
	}

	override fun onDestroy() {
		if (isCoveredByBiometric()) {
			appInterface.isAppInForeground.removeObserver(observer)
		}
		super.onDestroy()
	}

	override fun finish() {
		super<AppCompatActivity>.finish()
	}

	protected fun createBiometricPrompt(): BiometricPrompt {
		val executor = Executors.newSingleThreadExecutor()
		val activity: FragmentActivity = this // reference to activity
		val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

			override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
				super.onAuthenticationError(errorCode, errString)

				when (errorCode) {
					BiometricConstants.ERROR_NEGATIVE_BUTTON -> {
						activity.finish()
						appInterface.redirectToLogin()
					}
					BiometricConstants.ERROR_HW_NOT_PRESENT -> {
						Toast.makeText(
							applicationContext,
							"$errString ,TODO fallback authentication",
							Toast.LENGTH_SHORT
						).show()
					}
					else -> {
						activity.finish()
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
				activity.finish()
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
	@RequiresApi(Build.VERSION_CODES.N)
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
			.setUserAuthenticationValidityDurationSeconds(10)
			// Generated keys will be invalidated if the biometric templates are added more to user device
			.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)

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