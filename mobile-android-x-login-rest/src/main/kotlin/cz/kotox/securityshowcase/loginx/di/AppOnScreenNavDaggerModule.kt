package cz.kotox.routines.di

import androidx.lifecycle.ViewModel
import cz.kotox.routines.ui.MainActivity
import cz.kotox.routines.ui.MainFragment
import cz.kotox.routines.ui.MainViewModel
import cz.kotox.securityshowcase.core.di.ViewModelKey
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