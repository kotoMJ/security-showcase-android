package cz.koto.misak.securityshowcase.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import cz.kinst.jakub.viewmodelbinding.ViewModelBindingConfig
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.misak.securityshowcase.ui.BaseActivity
import cz.koto.misak.securityshowcase.ui.login.LoginViewModel.Companion.FORCE_SIGNUP_REQUEST

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun getViewModelBindingConfig() =
            ViewModelBindingConfig(R.layout.activity_login, LoginViewModel::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
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