package cz.koto.misak.securityshowcase.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import com.strv.keystorecompat.KeystoreCompat
import cz.kinst.jakub.viewmodelbinding.ViewModelBindingConfig
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.ActivityMainBinding
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseActivity
import cz.koto.misak.securityshowcase.ui.info.InfoFragment
import cz.koto.misak.securityshowcase.ui.info.SettingsFragment
import cz.koto.misak.securityshowcase.ui.login.LoginActivity
import cz.koto.misak.securityshowcase.utility.runOnLollipop
import cz.koto.misak.securityshowcase.utility.start
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    companion object {
        val RC_WIZARD_PAYMENT = 1
    }

    override fun getViewModelBindingConfig() = ViewModelBindingConfig<MainViewModel>(R.layout.activity_main, MainViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        runOnLollipop { KeystoreCompat.successSignUp() }

        if (savedInstanceState == null)
            switchToFragment(InfoFragment.newInstance(), false)

        binding.navigation.setOnNavigationItemSelectedListener { selectedItem ->
            when (selectedItem.itemId) {
                R.id.menu_info -> switchToFragment(InfoFragment.newInstance(), false)
                R.id.menu_settings -> switchToFragment(SettingsFragment.newInstance(), false)
                R.id.menu_logout -> {
                    CredentialStorage.performLogout()
                    activity.start<LoginActivity>()
                    activity.finish()
                }
            }
            true
        }

        setSupportActionBar(toolbar)
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
    }

}