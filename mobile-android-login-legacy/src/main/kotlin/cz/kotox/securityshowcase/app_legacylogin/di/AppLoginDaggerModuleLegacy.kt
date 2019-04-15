package cz.kotox.securityshowcase.app_legacylogin.di

import android.arch.lifecycle.ViewModel
import cz.kotox.securityshowcase.app_legacylogin.ui.MainActivityLegacy
import cz.kotox.securityshowcase.app_legacylogin.ui.MainFragmentLegacy
import cz.kotox.securityshowcase.app_legacylogin.ui.MainViewModelLegacy
import cz.kotox.securityshowcase.app_legacylogin.ui.SplashActivityLegacy
import cz.kotox.securityshowcase.module_core.di.ViewModelKeyLegacy
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AppLoginDaggerModuleLegacy {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivity(): MainActivityLegacy

	@ContributesAndroidInjector
	abstract fun contributeMainFragment(): MainFragmentLegacy

	@Binds
	@IntoMap
	@ViewModelKeyLegacy(MainViewModelLegacy::class)
	abstract fun bindMainViewModel(mainViewModel: MainViewModelLegacy): ViewModel

	@ContributesAndroidInjector()
	abstract fun contributeSplashActivity(): SplashActivityLegacy

}