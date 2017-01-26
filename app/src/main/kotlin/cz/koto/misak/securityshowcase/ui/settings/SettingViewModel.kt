package cz.koto.misak.securityshowcase.ui.settings

import android.databinding.ObservableBoolean
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.keystorecompat.utility.runSinceKitKat
import cz.koto.misak.keystorecompat.utility.showLockScreenSettings
import cz.koto.misak.securityshowcase.databinding.FragmentSettingsBinding
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseViewModel
import cz.koto.misak.securityshowcase.utility.Logcat
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SettingViewModel : BaseViewModel<FragmentSettingsBinding>() {

    val androidSecurityAvailable = ObservableBoolean(false)
    val androidSecuritySelectable = ObservableBoolean(false)

    override fun onViewModelCreated() {
        super.onViewModelCreated()
    }

    override fun onViewAttached(firstAttachment: Boolean) {
        super.onViewAttached(firstAttachment)

        setVisibility()


        runSinceKitKat {
            binding.settingsAndroidSecuritySwitch.isChecked = KeystoreCompat.hasCredentialsLoadable()
            binding.settingsAndroidSecuritySwitch.setOnCheckedChangeListener { switch, b ->
                if (b) {
                    binding.settingsAndroidSecuritySwitch.isEnabled = false
                    Flowable.fromCallable {
                        KeystoreCompat.storeCredentials(
                                "${CredentialStorage.getUserName()};${CredentialStorage.getPassword()}",
                                { Logcat.e("Store credentials failed!") })
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({}, {
                                it.printStackTrace()
                                binding.settingsAndroidSecuritySwitch.isEnabled = true
                            }, {
                                binding.settingsAndroidSecuritySwitch.isEnabled = true
                                /* DEV test to load stored credentials (don't forget to increase setUserAuthenticationValidityDurationSeconds() to fulfill this test!) */
                                //CredentialsKeystoreProvider.loadCredentials({ loaded -> Logcat.w("LOAD test %s", loaded) }, { Logcat.e("LOAD test FAILURE") }, false)
                            })
                } else {
                    KeystoreCompat.clearCredentials()
                }
            }
        }
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
