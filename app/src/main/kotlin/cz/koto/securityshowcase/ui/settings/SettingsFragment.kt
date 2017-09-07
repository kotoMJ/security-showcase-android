package cz.koto.securityshowcase.ui.info

import android.content.Intent
import android.os.Bundle
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.databinding.FragmentSettingsBinding
import cz.koto.securityshowcase.ui.BaseFragment
import cz.koto.securityshowcase.ui.settings.SettingViewModel


class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingViewModel>() {


	override fun onCreate(savedInstanceState: Bundle?) {
		setupViewModel(R.layout.fragment_settings, SettingViewModel::class.java)
		super.onCreate(savedInstanceState)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
	}


	companion object {
		fun newInstance() = SettingsFragment().apply {
			arguments = Bundle()
			arguments.putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, false)

//            arguments = Bundle().apply { TODO
//                putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, false)
//            }
		}

		fun newInstance(encryptionRequested: Boolean) = SettingsFragment().apply {
			arguments = Bundle()
			arguments.putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, encryptionRequested)

//			arguments = Bundle().apply { TODO
//                putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, encryptionRequested)
//            }
		}
	}
}