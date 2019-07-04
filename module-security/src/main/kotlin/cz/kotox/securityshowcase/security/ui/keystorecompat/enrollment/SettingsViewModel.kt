package cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import cz.kotox.keystorecompat.KeystoreCompat
import cz.kotox.keystorecompat.base.exception.ForceLockScreenKitKatException
import cz.kotox.keystorecompat.base.exception.ForceLockScreenMarshmallowException
import cz.kotox.keystorecompat.base.utility.forceAndroidAuth
import cz.kotox.securityshowcase.core.arch.BaseViewModel
import cz.kotox.securityshowcase.security.R
import cz.kotox.securityshowcase.security.entity.CredentialStorage
import timber.log.Timber
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
	private val appContext: Context,
	private val keystoreCompat: KeystoreCompat,
	private val credentialStorage: CredentialStorage
) : BaseViewModel() {

	val enrollmentInProgress: MutableLiveData<Boolean> = MutableLiveData(false)

	fun enrollKeystoreCompat(onIntentReady: (intent: Intent) -> Unit) {
		enrollmentInProgress.value = true
		keystoreCompat.storeSecret(
			credentialStorage.toStoreString(),
			{ exception ->
				when (exception) {
					is ForceLockScreenMarshmallowException -> {
						forceAndroidAuth(
							appContext.getString(R.string.kc_lock_screen_title),
							appContext.getString(R.string.kc_lock_screen_description),
							{ intent ->
								Timber.e("ForceLockScreenMarshmallowException.onIntentReady...")
								//TODO("ForceLockScreenMarshmallowException.onIntentReady... is NOT implemented!")
								//intent -> activity?.startActivityForResult(intent, MainActivity.FORCE_ENCRYPTION_REQUEST_M)
								onIntentReady.invoke(intent)
							},
							keystoreCompat.context)
					}
					is ForceLockScreenKitKatException -> {
					}
				}
				enrollmentInProgress.value = false
			},
			{
				enrollmentInProgress.value = false
			})
	}

}
