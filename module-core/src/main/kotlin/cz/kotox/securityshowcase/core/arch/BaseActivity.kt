package cz.kotox.securityshowcase.core.arch

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector, BaseUIScreen {

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

	override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
		return dispatchingAndroidInjector
	}

	override val baseActivity: BaseActivity get() = this
	override var lastSnackbar: Snackbar? = null

	override fun finish() {
		super<AppCompatActivity>.finish()
	}
}