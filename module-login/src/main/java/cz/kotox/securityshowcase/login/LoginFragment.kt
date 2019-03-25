package cz.kotox.securityshowcase.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.login.databinding.FragmentLoginBinding

class LoginFragment : BaseFragmentViewModel<LoginViewModel, FragmentLoginBinding>() {
//	override val baseActivity: BaseActivity
//		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun setupViewModel() = findViewModel<LoginViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentLoginBinding.inflate(inflater)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState)

//		binding.floatingActionButton.setOnClickListener {
//			//showSnackbar(binding.root, "hop")
//
//		}

		binding.loginButton.setOnClickListener {
			showSnackbar(binding.root, "Login action ...")
		}

		return view
	}
}