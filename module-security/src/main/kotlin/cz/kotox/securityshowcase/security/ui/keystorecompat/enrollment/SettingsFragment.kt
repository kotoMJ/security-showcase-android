package cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment

import android.view.LayoutInflater
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.security.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragmentViewModel<SettingsViewModel, FragmentSettingsBinding>() {
	override fun setupViewModel() = findViewModel<SettingsViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater)

}