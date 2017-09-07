package cz.koto.securityshowcase.ui.settings

import android.databinding.ObservableBoolean
import android.view.LayoutInflater
import cz.kinst.jakub.view.SimpleStatefulLayout
import cz.kinst.jakub.view.StatefulLayout
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.keystorecompat.exception.ForceLockScreenMarshmallowException
import cz.koto.keystorecompat.utility.forceAndroidAuth
import cz.koto.keystorecompat.utility.runSinceKitKat
import cz.koto.keystorecompat.utility.showLockScreenSettings
import cz.koto.securityshowcase.ContextProvider
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.databinding.FragmentSettingsBinding
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.ui.BaseViewModel
import cz.koto.securityshowcase.ui.StateListener
import cz.koto.securityshowcase.ui.main.MainActivity.Companion.FORCE_ENCRYPTION_REQUEST_M
import cz.koto.securityshowcase.utility.Logcat
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SettingViewModel : BaseViewModel<FragmentSettingsBinding>(), StateListener {

	val androidSecurityAvailable = ObservableBoolean(false)
	val androidSecuritySelectable = ObservableBoolean(false)
	val androidSecurityValue = ObservableBoolean(false)


	companion object {
		val EXTRA_ENCRYPTION_REQUEST_SCHEDULED = "EXTRA_ENCRYPTION_REQUEST_SCHEDULED"
	}

	lateinit var stateController: StatefulLayout.StateController

	override fun onViewModelCreated() {
		super.onViewModelCreated()
		stateController = StatefulLayout.StateController.create()
				.withState(SimpleStatefulLayout.State.PROGRESS, LayoutInflater.from(activity).inflate(R.layout.include_progress, null))
				.build()
		if (view.bundle.get(SettingViewModel.EXTRA_ENCRYPTION_REQUEST_SCHEDULED) == true) storeSecret()
	}

	override fun onViewAttached(firstAttachment: Boolean) {
		super.onViewAttached(firstAttachment)
		setVisibility()
	}

	override fun setProgress() {
		stateController.state = SimpleStatefulLayout.State.PROGRESS
	}


	override fun setContent() {
		stateController.state = SimpleStatefulLayout.State.CONTENT
	}

	private fun setVisibility() {
		runSinceKitKat {
			androidSecurityAvailable.set(KeystoreCompat.isKeystoreCompatAvailable())
			androidSecuritySelectable.set(KeystoreCompat.isSecurityEnabled())
		}
	}

	override fun onResume() {
		super.onResume()
		setVisibility()
	}

	fun onClickSecuritySettings() {
		showLockScreenSettings(context)
	}

	private fun storeSecret() {
		KeystoreCompat.clearCredentials()
		Flowable.fromCallable {
			KeystoreCompat.storeSecret(
					"${CredentialStorage.getUserName()};${CredentialStorage.getPassword()}",
					{
						Logcat.e("Store credentials failed!", it)
						if (it is ForceLockScreenMarshmallowException) {
							forceAndroidAuth(getString(R.string.kc_lock_screen_title), getString(R.string.kc_lock_screen_description),
									{ intent -> activity.startActivityForResult(intent, FORCE_ENCRYPTION_REQUEST_M) }, KeystoreCompat.context)
						}
					},
					{ Logcat.d("Credentials stored.") })
		}
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({}, {
					Logcat.e("Store credentials failed!", it)
					activity.showSnackBar(ContextProvider.getString(R.string.settings_security_store_failed))
					androidSecuritySelectable.set(true)
					androidSecurityValue.set(false)
				}, {
					androidSecuritySelectable.set(true)
					androidSecurityValue.set(true)
					/* DEV test to load stored credentials (don't forget to increase setUserAuthenticationValidityDurationSeconds() to fulfill this test!) */
					//CredentialsKeystoreProvider.loadCredentials({ loaded -> Logcat.w("LOAD test %s", loaded) }, { Logcat.e("LOAD test FAILURE") }, false)
				})
	}

	fun onCheckedChanged(checked: Boolean) {
		runSinceKitKat {
			if (checked) {
				androidSecuritySelectable.set(false)
				storeSecret()
			} else {
				KeystoreCompat.clearCredentials()
			}
		}
	}

}
