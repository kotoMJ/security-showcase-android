package cz.kotox.securityshowcase.core.arch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import cz.kotox.securityshowcase.core.AppInterface
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, BaseUIScreen {

	companion object {
		val BIOMETRIC_KEY = "biometric_key"
	}

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

	@Inject
	lateinit var appInterface: AppInterface


	override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
		return dispatchingAndroidInjector
	}

	override val baseActivity: BaseActivity get() = this
	override var lastSnackbar: Snackbar? = null

	public override fun onCreate(savedInstanceState: Bundle?) {
		//CrashlyticsUtility.setCurrentActivityKey(javaClass.simpleName)
		Timber.v(javaClass.simpleName)
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true) //VectorDrawables visible on KitKat
	}

	override fun finish() {
		super<AppCompatActivity>.finish()
	}

}