package cz.kotox.securityshowcase.login

import android.view.LayoutInflater
import cz.kotox.securityshowcase.core.arch.BaseActivity
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.login.databinding.FragmentLoginBinding

class LoginFragment : BaseFragmentViewModel<LoginViewModel, FragmentLoginBinding>() {
	override val baseActivity: BaseActivity
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun setupViewModel(): LoginViewModel {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun inflateBindingLayout(inflater: LayoutInflater): FragmentLoginBinding {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	//private val vmb by vmb<LoginViewModel, FragmentTextRecognitionBinding>(R.layout.fragment_login) { findViewModel(LoginViewModel::class.java) }

}