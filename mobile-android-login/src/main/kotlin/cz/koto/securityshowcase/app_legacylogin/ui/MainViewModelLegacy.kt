package cz.koto.securityshowcase.app_legacylogin.ui

import android.arch.lifecycle.MutableLiveData
import android.databinding.Bindable
import cz.koto.securityshowcase.core.FeatureCore
import cz.koto.securityshowcase.core.arch.BaseViewModelLegacy
import cz.koto.securityshowcase.core.database.preferences.PreferencesCommon
import cz.koto.securityshowcase.core.entity.AppVersionLegacy
import javax.inject.Inject

class MainViewModelLegacy @Inject constructor(appVersion: AppVersionLegacy) : BaseViewModelLegacy() {

//	@Inject
//	lateinit var appVersion: AppVersion

	@Inject
	lateinit var preferencesCore: PreferencesCommon

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