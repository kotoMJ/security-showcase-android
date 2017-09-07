package cz.koto.securityshowcase.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.securityshowcase.ui.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

	companion object {
		val FORCE_SIGNUP_REQUEST = 1111
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setupViewModel(R.layout.activity_login, LoginViewModel::class.java)
		super.onCreate(savedInstanceState)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == FORCE_SIGNUP_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				KeystoreCompat.increaseLockScreenCancel()
				activity.finish()
			} else {
				viewModel.onViewAttached(false)
			}
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}
}