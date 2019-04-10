package cz.koto.securityshowcase.ui.info

import android.arch.lifecycle.ViewModel
import cz.koto.securityshowcase.BuildConfig

class InfoViewModel : ViewModel() {

	fun getVersion() = "${BuildConfig.VERSION_CODE} - ${BuildConfig.VERSION_NAME}"

}