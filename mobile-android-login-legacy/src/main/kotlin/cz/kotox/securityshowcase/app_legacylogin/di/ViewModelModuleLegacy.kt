package cz.kotox.securityshowcase.app_legacylogin.di

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModuleLegacy {
	@Binds
	abstract fun bindViewModelFactory(factory: ViewModelFactoryLegacy): ViewModelProvider.Factory
}