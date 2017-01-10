package cz.koto.misak.securityshowcase.ui.login

import android.content.Intent
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.net.Uri
import com.strv.keystorecompat.KeystoreProvider
import com.strv.keystorecompat.utility.forceAndroidAuth
import cz.kinst.jakub.view.StatefulLayout
import cz.koto.misak.securityshowcase.ContextProvider
import cz.koto.misak.securityshowcase.SecurityConfig
import cz.koto.misak.securityshowcase.api.base.SecurityShowcaseApiProvider
import cz.koto.misak.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.misak.securityshowcase.model.AuthRequestSimple
import cz.koto.misak.securityshowcase.model.AuthResponseSimple
import cz.koto.misak.securityshowcase.model.base.ServerResponseObject
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.BaseViewModel
import cz.koto.misak.securityshowcase.ui.main.MainActivity
import cz.koto.misak.securityshowcase.utility.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel : BaseViewModel<ActivityLoginBinding>() {

    companion object {
        val FORCE_SIGNUP_REQUEST = 1111
    }

    val devAvailable = ObservableBoolean(SecurityConfig.isEndpointDev() && !SecurityConfig.isPackageRelease())
    val state = ObservableField(StatefulLayout.State.CONTENT)

    val username: ObservableField<String> = ObservableField()
    val password: ObservableField<String> = ObservableField()

    val showSignIn = ObservableBoolean(false)

    val userNameChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            showSignIn.set(username.get().isNotEmpty())
        }
    }

    var stringChallenge = ""

    var loginAt by longPref("last_login_at")

    override fun onViewModelCreated() {
        super.onViewModelCreated()
        username.addOnPropertyChangedCallback(userNameChanged)
    }

    override fun onViewAttached(firstAttachment: Boolean) {
        super.onViewAttached(firstAttachment)
        runOnLollipop {
            if (KeystoreProvider.hasCredentialsLoadable()) {
                KeystoreProvider.loadCredentials({ decryptResult ->
                    decryptResult.split(';').let {
                        username.set(it[0])
                        password.set(it[1])
                        signIn()
                    }
                }, { exception ->
                    Logcat.e(exception, "")
                    CredentialStorage.performLogout()
                    forceAndroidAuth("my title", "my desc", { intent -> activity.startActivityForResult(intent, FORCE_SIGNUP_REQUEST) })
                }, null)
            } else {
                Logcat.d("Use standard login.")
            }
        }
    }

    override fun onViewModelDestroyed() {
        super.onViewModelDestroyed()
        username.removeOnPropertyChangedCallback(userNameChanged)
    }

    fun signIn() {
        state.progress()
        SecurityShowcaseApiProvider.authProvider.loginSimple(AuthRequestSimple(
                username.get() ?: "",
                password.get() ?: ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onSuccessfulLogin(it) }, { state.content(); it.printStackTrace() })
    }

    private fun onSuccessfulLogin(it: ServerResponseObject<AuthResponseSimple>) =
            if (it.data?.successful ?: false) {
                CredentialStorage
                        .storeUser(it.data, username.get() ?: "", password.get() ?: "")
                loginAt = System.nanoTime()
                showMain()
            } else {
                state.content()
            }


    fun fillTest() {
        username.set(SecurityConfig.getTestUsername())
        password.set(SecurityConfig.getTestPass())
    }


    private fun showMain() {
        stringChallenge = ""
        activity.finish()
        activity.start<MainActivity>()
    }

    fun followGithub() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kotomisak/db-showcase-android"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ContextProvider.context.startActivity(intent)
    }
}