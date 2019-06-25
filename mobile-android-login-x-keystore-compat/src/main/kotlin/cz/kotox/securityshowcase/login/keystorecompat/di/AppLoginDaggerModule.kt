package cz.kotox.securityshowcase.login.keystorecompat.di

import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.di.ViewModelKey
import cz.kotox.securityshowcase.login.keystorecompat.ui.MainActivity
import cz.kotox.securityshowcase.login.keystorecompat.ui.MainFragment
import cz.kotox.securityshowcase.login.keystorecompat.ui.MainViewModel
import cz.kotox.securityshowcase.login.keystorecompat.ui.SplashActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AppLoginDaggerModule {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivity(): MainActivity

	@ContributesAndroidInjector
	abstract fun contributeMainFragment(): MainFragment

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

	@ContributesAndroidInjector()
	abstract fun contributeSplashActivity(): SplashActivity

}