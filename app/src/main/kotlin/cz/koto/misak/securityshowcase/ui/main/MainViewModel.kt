package cz.koto.misak.securityshowcase.ui.main

import android.databinding.ObservableBoolean
import cz.koto.misak.securityshowcase.databinding.ActivityMainBinding
import cz.koto.misak.securityshowcase.ui.BaseViewModel
import cz.koto.misak.securityshowcase.ui.login.LoginActivity
import cz.koto.misak.securityshowcase.utility.ApplicationEvent
import cz.koto.misak.securityshowcase.utility.applicationEvents
import cz.koto.misak.securityshowcase.utility.start

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