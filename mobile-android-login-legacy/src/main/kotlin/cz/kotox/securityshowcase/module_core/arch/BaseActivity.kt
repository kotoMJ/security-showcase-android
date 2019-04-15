package cz.kotox.securityshowcase.module_core.arch

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, BaseUIScreenLegacy {

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

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