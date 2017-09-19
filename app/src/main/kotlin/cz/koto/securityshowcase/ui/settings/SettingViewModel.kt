package cz.koto.securityshowcase.ui.settings

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import cz.koto.keystorecompat.utility.showLockScreenSettings
import cz.koto.securityshowcase.ui.StateListener


class SettingViewModel(val context: Application) : AndroidViewModel(context), StateListener {

	val androidSecurityAvailable = ObservableBoolean(false)
	val androidSecuritySelectable = ObservableBoolean(false)
	val androidSecurityValue = ObservableBoolean(false)


	companion object {
		val EXTRA_ENCRYPTION_REQUEST_SCHEDULED = "EXTRA_ENCRYPTION_REQUEST_SCHEDULED"
	}

	override fun setProgress() {
		//stateController.state = SimpleStatefulLayout.State.PROGRESS
	}


	override fun setContent() {
		//stateController.state = SimpleStatefulLayout.State.CONTENT
	}

	fun onClickSecuritySettings() {
		showLockScreenSettings(context)
	}

}
