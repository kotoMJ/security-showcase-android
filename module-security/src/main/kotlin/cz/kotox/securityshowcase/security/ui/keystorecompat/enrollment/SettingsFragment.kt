package cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.kotox.keystorecompat.base.utility.showLockScreenSettings
import cz.kotox.securityshowcase.core.arch.BaseFragmentViewModel
import cz.kotox.securityshowcase.security.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragmentViewModel<SettingsViewModel, FragmentSettingsBinding>() {
	override fun setupViewModel() = findViewModel<SettingsViewModel>()

	override fun inflateBindingLayout(inflater: LayoutInflater): FragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = super.onCreateView(inflater, container, savedInstanceState)

		binding.lockScreenLayout.lockScreenSettingsLink.setOnClickListener {
			showLockScreenSettings(context)
		}

		return view
	}
}