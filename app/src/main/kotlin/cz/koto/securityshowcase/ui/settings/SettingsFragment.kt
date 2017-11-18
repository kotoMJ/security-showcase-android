package cz.koto.securityshowcase.ui.info

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.koto.keystorecompat.base.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.utility.forceAndroidAuth
import cz.koto.keystorecompat.utility.runSinceKitKat
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.SecurityApplication
import cz.koto.securityshowcase.databinding.FragmentSettingsBinding
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.ui.BaseArchFragment
import cz.koto.securityshowcase.ui.main.MainActivity
import cz.koto.securityshowcase.ui.settings.SettingViewModel
import cz.koto.securityshowcase.ui.settings.SettingsView
import cz.koto.securityshowcase.utility.Logcat
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SettingsFragment : BaseArchFragment(), SettingsView {

	private lateinit var viewDataBinding: FragmentSettingsBinding
	private lateinit var viewModel: SettingViewModel
	private val keystoreCompat by lazy { (activity.application as SecurityApplication).keystoreCompat }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		savedInstanceState?.let {
			if (it.get(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED) == true) {
				storeSecret()
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View? {
		viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
		viewDataBinding = FragmentSettingsBinding.inflate(inflater, container, false)
		viewDataBinding.viewModel = viewModel
		viewDataBinding.view = this

		setHasOptionsMenu(true)
		return viewDataBinding.root
	}


	override fun onResume() {
		super.onResume()
		setVisibility()
	}

	companion object {
		fun newInstance() = SettingsFragment().apply {
			arguments = Bundle()
			arguments.putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, false)

//            arguments = Bundle().apply { TODO
//                putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, false)
//            }
		}

		fun newInstance(encryptionRequested: Boolean) = SettingsFragment().apply {
			arguments = Bundle()
			arguments.putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, encryptionRequested)

//			arguments = Bundle().apply { TODO
//                putBoolean(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED, encryptionRequested)
//            }
		}
	}

	private fun setVisibility() {
		runSinceKitKat {
			viewModel.androidSecurityAvailable.set(keystoreCompat.isKeystoreCompatAvailable())
			viewModel.androidSecuritySelectable.set(keystoreCompat.isSecurityEnabled())
			viewModel.androidSecurityValue.set(keystoreCompat.hasSecretLoadable())
		}
	}

	private fun storeSecret() {
		keystoreCompat.clearCredentials()
		Flowable.fromCallable {
			keystoreCompat.storeSecret(
					"${CredentialStorage.getUserName()};${CredentialStorage.getPassword()}",
					{
						Logcat.e("Store credentials failed!", it)
						if (it is ForceLockScreenMarshmallowException) {
							forceAndroidAuth(getString(R.string.kc_lock_screen_title), getString(R.string.kc_lock_screen_description),
									{ intent -> activity.startActivityForResult(intent, MainActivity.FORCE_ENCRYPTION_REQUEST_M) }, keystoreCompat.context)
						}
					},
					{ Logcat.d("Credentials stored.") })
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({}, {
					Logcat.e("Store credentials failed!", it)
					//TODO solve this using arch
					//activity.showSnackBar(ContextProvider.getString(R.string.settings_security_store_failed))
					viewModel.androidSecuritySelectable.set(true)
					viewModel.androidSecurityValue.set(false)
				}, {
					viewModel.androidSecuritySelectable.set(true)
					viewModel.androidSecurityValue.set(true)
					/* DEV test to load stored credentials (don't forget to increase setUserAuthenticationValidityDurationSeconds() to fulfill this test!) */
					//CredentialsKeystoreProvider.loadCredentials({ loaded -> Logcat.w("LOAD test %s", loaded) }, { Logcat.e("LOAD test FAILURE") }, false)
				})
	}

	override fun onCheckedChanged(checked: Boolean) {
		runSinceKitKat {
			if (checked) {
				viewModel.androidSecuritySelectable.set(false)
				storeSecret()
			} else {
				keystoreCompat.deactivate()
			}
		}
	}
}