package cz.kotox.securityshowcase.login.ui.keystorecompat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.login.databinding.FragmentLoginBinding

class LoginCompatFragment : BaseFragmentViewModel<LoginCompatViewModel, FragmentLoginBinding>() {
//	override val baseActivity: BaseActivity
//		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun setupViewModel() = findViewModel<LoginCompatViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentLoginBinding.inflate(inflater)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState)

//		binding.floatingActionButton.setOnClickListener {
//			//showSnackbar(binding.root, "hop")
//
//		}

		binding.loginButton.setOnClickListener {

			//TODO MJ - check for autorization and stay or go to the app

			activity?.finish()
			appInterface.startApp()
		}

		binding.fillTestCredentialsButton.setOnClickListener {
			binding.inputEmailEditText.setText(viewModel.testCredentials.email)
			binding.inputPasswordEditText.setText(viewModel.testCredentials.password)
		}

		return view
	}
}
