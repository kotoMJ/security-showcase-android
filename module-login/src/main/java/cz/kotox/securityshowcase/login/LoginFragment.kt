package cz.kotox.securityshowcase.login

import android.view.LayoutInflater
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.login.databinding.FragmentLoginBinding

class LoginFragment : BaseFragmentViewModel<LoginViewModel, FragmentLoginBinding>() {
//	override val baseActivity: BaseActivity
//		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

	override fun setupViewModel() = findViewModel<LoginViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater) = FragmentLoginBinding.inflate(inflater)

}