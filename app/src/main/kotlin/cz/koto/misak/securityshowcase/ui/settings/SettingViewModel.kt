package cz.koto.misak.securityshowcase.ui.settings

import android.databinding.ObservableBoolean
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.keystorecompat.exception.ForceLockScreenMarshmallowException
import cz.koto.misak.keystorecompat.utility.forceAndroidAuth
import cz.koto.misak.keystorecompat.utility.runSinceKitKat
import cz.koto.misak.keystorecompat.utility.showLockScreenSettings
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.databinding.FragmentSettingsBinding
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseViewModel
import cz.koto.misak.securityshowcase.ui.main.MainActivity.Companion.FORCE_ENCRYPTION_REQUEST
import cz.koto.misak.securityshowcase.utility.Logcat
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SettingViewModel : BaseViewModel<FragmentSettingsBinding>() {

    val androidSecurityAvailable = ObservableBoolean(false)
    val androidSecuritySelectable = ObservableBoolean(false)

    companion object {
        val EXTRA_ENCRYPTION_REQUESTED = "EXTRA_ENCRYPTION_REQUESTED"
    }

    override fun onViewModelCreated() {
        super.onViewModelCreated()
        if (view.bundle.get(SettingViewModel.EXTRA_ENCRYPTION_REQUESTED) == true) storeSecret()
    }

    override fun onViewAttached(firstAttachment: Boolean) {
        super.onViewAttached(firstAttachment)
        setVisibility()
        runSinceKitKat {
            binding.settingsAndroidSecuritySwitch.isChecked = KeystoreCompat.hasSecretLoadable()
            binding.settingsAndroidSecuritySwitch.setOnCheckedChangeListener { switch, b ->
                if (b) {
                    binding.settingsAndroidSecuritySwitch.isEnabled = false
                    storeSecret()
                } else {
                    KeystoreCompat.clearCredentials()
                }
            }
        }
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
                                    { intent -> activity.startActivityForResult(intent, FORCE_ENCRYPTION_REQUEST) }, KeystoreCompat.context)
                        }
                    },
                    { Logcat.d("Credentials stored.") })
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {
                    Logcat.e("Store credentials failed!", it)
                    activity.showSnackBar(ContextProvider.getString(R.string.settings_security_store_failed))
                    binding.settingsAndroidSecuritySwitch.isEnabled = true
                    binding.settingsAndroidSecuritySwitch.isChecked = false
                }, {
                    binding.settingsAndroidSecuritySwitch.isEnabled = true
                    binding.settingsAndroidSecuritySwitch.isChecked = true
                    /* DEV test to load stored credentials (don't forget to increase setUserAuthenticationValidityDurationSeconds() to fulfill this test!) */
                    //CredentialsKeystoreProvider.loadCredentials({ loaded -> Logcat.w("LOAD test %s", loaded) }, { Logcat.e("LOAD test FAILURE") }, false)
                })
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
}
