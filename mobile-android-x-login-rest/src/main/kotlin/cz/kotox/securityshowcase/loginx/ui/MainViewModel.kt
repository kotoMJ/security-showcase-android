package cz.kotox.routines.ui

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import cz.kotox.securityshowcase.core.FeatureCore
import cz.kotox.securityshowcase.core.PreferencesCore
import cz.kotox.securityshowcase.core.arch.ObservableViewModel
import cz.kotox.securityshowcase.core.entity.AppVersion
import javax.inject.Inject

class MainViewModel @Inject constructor(appVersion: AppVersion) : ObservableViewModel() {

//	@Inject
//	lateinit var appVersion: AppVersion

	@Inject
	lateinit var preferencesCore: PreferencesCore

	val token: MutableLiveData<String> = MutableLiveData()

	val notificationsEnabled: Boolean @Bindable get() = FeatureCore.notificationsEnabled

	val appVersionString = "${appVersion.versionName} (${appVersion.versionCode})"

//	val appVersionString = preferencesCore.sampleToken

	init {
		token.value = "testicek"
	}

//	val token : String = "ddd"

//	fun getToken(): LiveData<String> {
//		val ret = MutableLiveData<String>()
//		ret.value = "..."
//		return ret
//	}

}