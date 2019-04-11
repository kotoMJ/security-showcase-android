package cz.koto.securityshowcase.ui.main

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatDelegate
import cz.koto.securityshowcase.ContextProvider
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.SecurityApplication
import cz.koto.securityshowcase.databinding.ActivityMainBinding
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.ui.BaseArchActivity
import cz.koto.securityshowcase.ui.info.InfoFragment
import cz.koto.securityshowcase.ui.info.SettingsFragment
import cz.koto.securityshowcase.ui.login.LoginActivity
import cz.koto.securityshowcase.utility.ApplicationEvent
import cz.koto.securityshowcase.utility.applicationEvents
import cz.koto.securityshowcase.utility.start
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : BaseArchActivity() {

	companion object {
		val FORCE_ENCRYPTION_REQUEST_M = 1112
	}

	private val keystoreCompat by lazy { (application as SecurityApplication).keystoreCompat }

	private lateinit var viewModel: MainViewModel
	private lateinit var viewDataBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
		viewDataBinding = ActivityMainBinding.inflate(layoutInflater)
		viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
		viewDataBinding.viewModel = viewModel
		viewDataBinding.executePendingBindings()

		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		super.onCreate(savedInstanceState)
		keystoreCompat.lockScreenSuccessful()

		if (savedInstanceState == null)
			switchToFragment(InfoFragment.newInstance(), false)

		viewDataBinding.navigation.setOnNavigationItemSelectedListener { selectedItem ->
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
					start<LoginActivity>()
					finish()
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

		bind(applicationEvents) {
			when (it) {
				is ApplicationEvent.RequestLogin -> {
					//TODO use SingleLiveEvent from arch components instead.
					finish()
					ActivityCompat.startActivity(applicationContext, Intent(this, LoginActivity::class.java), null)
				}
			}
		}
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
		if (requestCode == FORCE_ENCRYPTION_REQUEST_M) {
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