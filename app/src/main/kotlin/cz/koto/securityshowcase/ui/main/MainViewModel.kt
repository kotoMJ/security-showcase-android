package cz.koto.securityshowcase.ui.main

import android.databinding.ObservableBoolean
import cz.koto.securityshowcase.databinding.ActivityMainBinding
import cz.koto.securityshowcase.ui.BaseViewModel
import cz.koto.securityshowcase.ui.login.LoginActivity
import cz.koto.securityshowcase.utility.ApplicationEvent
import cz.koto.securityshowcase.utility.applicationEvents
import cz.koto.securityshowcase.utility.start

class MainViewModel : BaseViewModel<ActivityMainBinding>() {

	val progress = ObservableBoolean(false)

	override fun onViewModelCreated() {
		super.onViewModelCreated()

		bind(applicationEvents) {
			when (it) {
				is ApplicationEvent.RequestLogin -> showSignUp()
			}
		}
	}

	private fun showSignUp() {
		activity.finish()
		activity.start<LoginActivity>()
	}

	override fun onResume() {
		super.onResume()
	}

	override fun onViewModelDestroyed() {
		super.onViewModelDestroyed()
	}
}