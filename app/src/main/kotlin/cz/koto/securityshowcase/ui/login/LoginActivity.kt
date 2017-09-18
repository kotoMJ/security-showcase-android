package cz.koto.securityshowcase.ui.login

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import cz.koto.keystorecompat.KeystoreCompat
import cz.koto.keystorecompat.exception.ForceLockScreenKitKatException
import cz.koto.keystorecompat.utility.forceAndroidAuth
import cz.koto.keystorecompat.utility.runSinceKitKat
import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.databinding.ActivityLoginBinding
import cz.koto.securityshowcase.storage.CredentialStorage
import cz.koto.securityshowcase.ui.main.MainActivity
import cz.koto.securityshowcase.utility.ApplicationEvent
import cz.koto.securityshowcase.utility.Logcat
import cz.koto.securityshowcase.utility.applicationEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class LoginActivity : AppCompatActivity() {


	companion object {
		val FORCE_SIGNUP_REQUEST = 1111
	}

	private lateinit var viewModel: LoginViewModel
	private lateinit var viewDataBinding: ActivityLoginBinding

	val detached = PublishSubject.create<Unit>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
//		viewDataBinding = DataBindingUtil.inflate<ActivityLoginBinding>(layoutInflater, R.layout.activity_login, null, false).apply{
//			//Use this in fragment or case where you don't request viewModel directly.
//			//viewmodel = (activity as TasksActivity).obtainViewModel()
//		}
		viewDataBinding = ActivityLoginBinding.inflate(layoutInflater)
		viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
		viewDataBinding.viewModel = viewModel
		viewDataBinding.executePendingBindings()


		bind(applicationEvents) {
			when (it) {
				is ApplicationEvent.RequestMain -> {
					//TODO use SingleLiveEvent from arch components instead.
					finish()
					ActivityCompat.startActivity(this, Intent(this, MainActivity::class.java), null)
				}
			}
		}
		onLoginDisplayed(true)

	}

	override fun onDestroy() {
		detached.onNext(Unit)
		super.onDestroy()
	}

	protected fun <A> bind(source: io.reactivex.Observable<A>, action: (A) -> Unit) = source
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.takeUntil(detached)
			.subscribe(action)


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == FORCE_SIGNUP_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				KeystoreCompat.increaseLockScreenCancel()
				this.finish()
			} else {
				onLoginDisplayed(false)
			}
		} else
			super.onActivityResult(requestCode, resultCode, data)
	}

	fun onLoginDisplayed(firstAttachment: Boolean) {
		runSinceKitKat {
			if (KeystoreCompat.hasSecretLoadable()) {
				KeystoreCompat.loadSecretAsString({ decryptResult ->
					decryptResult.split(';').let {
						viewModel?.email?.set(it[0])
						viewModel?.password?.set(it[1])
						viewModel?.signInGql()
//						viewDataBinding.viewModel?.email?.set(it[0])
//						viewDataBinding.viewModel?.password?.set(it[1])
//						viewDataBinding.viewModel?.signInGql()
					}
				}, { exception ->
					CredentialStorage.dismissForceLockScreenFlag()
					if (exception is ForceLockScreenKitKatException) {
						this.startActivityForResult(exception.lockIntent, FORCE_SIGNUP_REQUEST)
					} else {
						Logcat.e(exception, "")
						CredentialStorage.performLogout()
						forceAndroidAuth(getString(R.string.kc_lock_screen_title), getString(R.string.kc_lock_screen_description),
								{ intent -> this.startActivityForResult(intent, FORCE_SIGNUP_REQUEST) },
								KeystoreCompat.context)
					}
				}, CredentialStorage.forceLockScreenFlag)
			} else {
				Logcat.d("Use standard login.")
			}
		}
	}
}