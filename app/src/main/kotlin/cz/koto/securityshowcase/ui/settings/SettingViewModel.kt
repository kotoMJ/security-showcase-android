package cz.koto.securityshowcase.ui.settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import cz.koto.keystorecompat.utility.showLockScreenSettings


class SettingViewModel(val context: Application) : AndroidViewModel(context) {

	val androidSecurityAvailable = ObservableBoolean(false)
	val androidSecuritySelectable = ObservableBoolean(false)
	val androidSecurityValue = ObservableBoolean(false)


	companion object {
		val EXTRA_ENCRYPTION_REQUEST_SCHEDULED = "EXTRA_ENCRYPTION_REQUEST_SCHEDULED"
	}

	fun onClickSecuritySettings() {
		showLockScreenSettings(context)
	}

}
