package cz.kotox.securityshowcase.loginx.di

import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.di.ViewModelKey
import cz.kotox.securityshowcase.loginx.ui.MainActivity
import cz.kotox.securityshowcase.loginx.ui.MainFragment
import cz.kotox.securityshowcase.loginx.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AppOnScreenNavDaggerModule {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivity(): MainActivity

	@ContributesAndroidInjector
	abstract fun contributeMainFragment(): MainFragment

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun bindSettingsViewModel(settingsViewModel: MainViewModel): ViewModel

}