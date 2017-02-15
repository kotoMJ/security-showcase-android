package cz.koto.misak.securityshowcase.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import cz.kinst.jakub.viewmodelbinding.ViewModelBindingConfig
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.keystorecompat.utility.runSinceKitKat
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.ActivityMainBinding
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseActivity
import cz.koto.misak.securityshowcase.ui.info.InfoFragment
import cz.koto.misak.securityshowcase.ui.info.SettingsFragment
import cz.koto.misak.securityshowcase.ui.login.LoginActivity
import cz.koto.misak.securityshowcase.utility.start
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    companion object {
        val FORCE_ENCRYPTION_REQUEST = 1112
    }

    override fun getViewModelBindingConfig() = ViewModelBindingConfig<MainViewModel>(R.layout.activity_main, MainViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)
        runSinceKitKat { KeystoreCompat.lockScreenSuccessful() }

        if (savedInstanceState == null)
            switchToFragment(InfoFragment.newInstance(), false)

        binding.navigation.setOnNavigationItemSelectedListener { selectedItem ->
            var ret: Boolean = false
            when (selectedItem.itemId) {
                R.id.menu_info -> {
                    switchToFragment(InfoFragment.newInstance(), false)
                    ret = true
                }
                R.id.menu_settings -> {
                    switchToFragment(SettingsFragment.newInstance(), false)
                    ret = true
                }
                R.id.menu_logout -> {
                    CredentialStorage.forceLockScreenFlag()
                    CredentialStorage.performLogout()
                    activity.start<LoginActivity>()
                    activity.finish()
                    ret = true
                }
                R.id.menu_gihub -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kotomisak/security-showcase-android"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ContextProvider.context.startActivity(intent)
                    ret = false
                }
            }
            ret
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FORCE_ENCRYPTION_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                switchToFragment(SettingsFragment.newInstance())
            } else if (resultCode == Activity.RESULT_OK) {
                switchToFragment(SettingsFragment.newInstance(true))
            } else {
                switchToFragment(SettingsFragment.newInstance())
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

}