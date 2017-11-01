package cz.koto.securityshowcase.ui.login

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import cz.koto.keystorecompat.exception.ForceLockScreenKitKatException
import cz.koto.keystorecompat.utility.forceAndroidAuth
import cz.koto.keystorecompat.utility.runSinceKitKat
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.SecurityApplication
import cz.koto.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.ui.BaseArchActivity
import cz.koto.securityshowcase.ui.main.MainActivity
import cz.koto.securityshowcase.utility.ApplicationEvent
import cz.koto.securityshowcase.utility.Logcat
import cz.koto.securityshowcase.utility.applicationEvents

class LoginActivity : BaseArchActivity() {


	companion object {
		val FORCE_SIGNUP_REQUEST = 1111
	}

	private val keystoreCompat by lazy { (application as SecurityApplication).keystoreCompat }

	private lateinit var viewModel: LoginViewModel
	private lateinit var viewDataBinding: ActivityLoginBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
		viewDataBinding = ActivityLoginBinding.inflate(layoutInflater)
		viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
		viewDataBinding.viewModel = viewModel
		viewDataBinding.executePendingBindings()


		bind(applicationEvents) {
			when (it) {
				is ApplicationEvent.RequestMain -> {
					//TODO use SingleLiveEvent from arch components instead.
					finish()

					val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
						addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					}
					ActivityCompat.startActivity(applicationContext, mainActivityIntent, null)
				}
			}
		}
		onLoginDisplayed(true)

	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == FORCE_SIGNUP_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				(application as SecurityApplication).keystoreCompat.increaseLockScreenCancel()
				this.finish()
			} else {
				onLoginDisplayed(false)
			}
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}

	fun onLoginDisplayed(firstAttachment: Boolean) {
		runSinceKitKat {
			if (keystoreCompat.hasSecretLoadable()) {
				keystoreCompat.loadSecretAsString({ decryptResult ->
					decryptResult.split(';').let {
						viewModel?.email?.set(it[0])
						viewModel?.password?.set(it[1])
						viewModel?.signInGql()
					}
				}, { exception ->
					CredentialStorage.dismissForceLockScreenFlag()
					if (exception is ForceLockScreenKitKatException) {
						this.startActivityForResult(exception.lockIntent, FORCE_SIGNUP_REQUEST)
					} else {
						Logcat.e(exception, "")
						CredentialStorage.performLogout()
						forceAndroidAuth(getString(R.string.kc_lock_screen_title), getString(R.string.kc_lock_screen_description),
								{ intent -> this.startActivityForResult(intent, FORCE_SIGNUP_REQUEST) },
								keystoreCompat.context)
					}
				}, CredentialStorage.forceLockScreenFlag)
			} else {
				Logcat.d("Use standard login.")
			}
		}
	}
}