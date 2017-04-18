package cz.koto.misak.securityshowcase.ui.login

import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.net.Uri
import cz.kinst.jakub.view.StatefulLayout
import cz.koto.misak.keystorecompat.KeystoreCompat
import cz.koto.misak.keystorecompat.exception.ForceLockScreenKitKatException
import cz.koto.misak.keystorecompat.utility.forceAndroidAuth
import cz.koto.misak.keystorecompat.utility.runSinceKitKat
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.R
import cz.koto.misak.securityshowcase.SecurityConfig
import cz.koto.misak.securityshowcase.api.base.SecurityShowcaseApiProvider
import cz.koto.misak.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.misak.securityshowcase.model.AuthRequestSimple
import cz.koto.misak.securityshowcase.model.AuthResponseSimple
import cz.koto.misak.securityshowcase.model.base.ServerResponseObject
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseViewModel
import cz.koto.misak.securityshowcase.ui.login.LoginActivity.Companion.FORCE_SIGNUP_REQUEST
import cz.koto.misak.securityshowcase.ui.main.MainActivity
import cz.koto.misak.securityshowcase.utility.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel : BaseViewModel<ActivityLoginBinding>() {


    val devAvailable = ObservableBoolean(SecurityConfig.isEndpointDev() && !SecurityConfig.isPackageRelease())
    val state = ObservableField(StatefulLayout.State.CONTENT)

    val email: ObservableField<String> = ObservableField()
    val password: ObservableField<String> = ObservableField()

    val showSignIn = ObservableBoolean(false)

    val userNameChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            showSignIn.set(email.get().isNotEmpty())
        }
    }

    var stringChallenge = ""

    var loginAt by longPref("last_login_at")

    override fun onViewModelCreated() {
        super.onViewModelCreated()
        CredentialStorage.forceLockScreenFlag()
        email.addOnPropertyChangedCallback(userNameChanged)
    }

    override fun onViewAttached(firstAttachment: Boolean) {
        super.onViewAttached(firstAttachment)
        runSinceKitKat {
            if (KeystoreCompat.hasSecretLoadable()) {
                KeystoreCompat.loadSecretAsString({ decryptResult ->
                    decryptResult.split(';').let {
                        email.set(it[0])
                        password.set(it[1])
                        signIn()
                    }
                }, { exception ->
                    CredentialStorage.dismissForceLockScreenFlag()
                    if (exception is ForceLockScreenKitKatException) {
                        activity.startActivityForResult(exception.lockIntent, FORCE_SIGNUP_REQUEST)
                    } else {
                        Logcat.e(exception, "")
                        CredentialStorage.performLogout()
                        forceAndroidAuth(getString(R.string.kc_lock_screen_title), getString(R.string.kc_lock_screen_description),
                                { intent -> activity.startActivityForResult(intent, FORCE_SIGNUP_REQUEST) },
                                KeystoreCompat.context)
                    }
                }, CredentialStorage.forceLockScreenFlag)
            } else {
                Logcat.d("Use standard login.")
            }
        }
    }

    override fun onViewModelDestroyed() {
        super.onViewModelDestroyed()
        email.removeOnPropertyChangedCallback(userNameChanged)
    }

    fun signIn() {
        state.progress()
        SecurityShowcaseApiProvider.authProvider.loginSimple(AuthRequestSimple(
                email.get() ?: "",
                password.get() ?: ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccessfulLogin(it) }, { state.content(); it.printStackTrace() })
    }

    private fun onSuccessfulLogin(it: AuthResponseSimple) =
            if (it?.idToken == null) {


                CredentialStorage
                        .storeUser(it, email.get() ?: "", password.get() ?: "")
                loginAt = System.nanoTime()
                showMain()
            } else {
                state.content()
            }


    fun fillTest() {
        email.set(SecurityConfig.getTestEmail())
        password.set(SecurityConfig.getTestPass())
    }


    private fun showMain() {
        stringChallenge = ""
        activity.finish()
        activity.start<MainActivity>()
    }

    fun followGithub() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kotomisak/security-showcase-android"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextProvider.context.startActivity(intent)
    }
}