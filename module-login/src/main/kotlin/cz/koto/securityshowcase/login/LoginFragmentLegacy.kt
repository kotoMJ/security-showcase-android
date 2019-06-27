package cz.koto.securityshowcase.module_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.koto.securityshowcase.databinding.FragmentLoginLegacyBinding
import cz.koto.securityshowcase.module_core.arch.BaseFragmentViewModelLegacy

class LoginFragmentLegacy : BaseFragmentViewModelLegacy<LoginViewModelLegacy, FragmentLoginLegacyBinding>() {
//	override val baseActivity: BaseActivity
//		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun setupViewModel() = findViewModel<LoginViewModelLegacy>()

	override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentLoginLegacyBinding.inflate(inflater)

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