package cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment

import android.app.Application
import android.os.Build
import androidx.databinding.ObservableBoolean
import cz.kotox.keystorecompat.base.utility.showLockScreenSettings
import cz.kotox.securityshowcase.core.arch.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private val context: Application) : BaseViewModel() {

	//TODO MJ - livedata!!!
	val androidSecurityAvailable = ObservableBoolean(false)
	val androidSecuritySelectable = ObservableBoolean(false)
	val androidSecurityValue = ObservableBoolean(false)

	companion object {
		val EXTRA_ENCRYPTION_REQUEST_SCHEDULED = "EXTRA_ENCRYPTION_REQUEST_SCHEDULED"
	}

	fun onClickSecuritySettings() {
		showLockScreenSettings(context) //TODO MJ - this is from android-keystore-compat-base
	}

	fun isKitkat(): Boolean {
		return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT
	}
}
