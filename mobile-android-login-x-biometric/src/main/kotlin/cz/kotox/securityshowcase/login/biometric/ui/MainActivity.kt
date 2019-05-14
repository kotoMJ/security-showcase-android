package cz.kotox.securityshowcase.login.biometric.ui

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricConstants
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cz.kotox.securityshowcase.R
import cz.kotox.securityshowcase.core.AppInterface
import cz.kotox.securityshowcase.core.arch.BaseActivity
import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon
import timber.log.Timber
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivity : BaseActivity() {

	@Inject
	lateinit var preferencesCore: PreferencesCommon

	lateinit var biometricPrompt: BiometricPrompt

	@Inject
	lateinit var appInterface: AppInterface

	@RequiresApi(Build.VERSION_CODES.M)
	val observer = Observer<Boolean>() {
		if (it) {
			try {
				initSignature("userId")
			} catch (unae: UserNotAuthenticatedException) {
				biometricPrompt.authenticate(promptInfo)
			} catch (th: Throwable) {
				biometricPrompt.authenticate(promptInfo)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		val host: NavHostFragment = supportFragmentManager
			.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

		// Set up Action Bar
		val navController = host.navController
		setupActionBar(navController)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			val dest: String = try {
				resources.getResourceName(destination.id)
			} catch (e: Throwable) {
				Integer.toString(destination.id)
			}

			Timber.d("Navigated to %s", dest)
		}

		biometricPrompt = createBiometricPrompt()

		try {
			//TODO temporarily always enroll
			val keyPair = generateKeyPair("userId", true)
		} catch (unae: UserNotAuthenticatedException) {
			biometricPrompt.authenticate(promptInfo)
		} catch (th: Throwable) {
			biometricPrompt.authenticate(promptInfo)
		}

		appInterface.isAppInForeground.observe(this, observer)
	}

	override fun onDestroy() {
		appInterface.isAppInForeground.removeObserver(observer)
		super.onDestroy()
	}

	private fun setupActionBar(navController: NavController) {
		NavigationUI.setupActionBarWithNavController(this, navController)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val retValue = super.onCreateOptionsMenu(menu)
		menuInflater.inflate(R.menu.menu_overflow, menu)
		return retValue
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Have the NavHelper look for an action or destination matching the menu
		// item id and navigate there if found.
		// Otherwise, bubble up to the parent.
		return NavigationUI.onNavDestinationSelected(item,
			Navigation.findNavController(this, R.id.my_nav_host_fragment))
			|| super.onOptionsItemSelected(item)
	}

	protected fun createBiometricPrompt(): BiometricPrompt {
		val executor = Executors.newSingleThreadExecutor()
		val activity: FragmentActivity = this // reference to activity
		val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {

			override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
				super.onAuthenticationError(errorCode, errString)

				when (errorCode) {
					BiometricConstants.ERROR_NEGATIVE_BUTTON -> { /*user clicked negative button, do nothing*/
						appInterface.redirectToLogin()
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
//						runOnUiThread {
//							Toast.makeText(applicationContext, errString, Toast.LENGTH_SHORT).show()
//						}
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
			.setUserAuthenticationValidityDurationSeconds(5)
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