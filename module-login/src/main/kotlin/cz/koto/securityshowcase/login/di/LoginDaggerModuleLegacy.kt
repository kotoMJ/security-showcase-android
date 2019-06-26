package cz.kotox.securityshowcase.module_login.di

import android.arch.lifecycle.ViewModel
import cz.kotox.securityshowcase.module_core.database.preferences.LocalPreferences
import cz.kotox.securityshowcase.module_core.di.ViewModelKeyLegacy
import cz.kotox.securityshowcase.module_login.LoginActivityLegacy
import cz.kotox.securityshowcase.module_login.LoginFragmentLegacy
import cz.kotox.securityshowcase.module_login.LoginViewModelLegacy
import cz.kotox.securityshowcase.module_login.database.preferences.PreferencesLoginLegacy
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet

@Module
abstract class LoginDaggerModuleLegacy {
	@Binds
	@IntoSet
	abstract fun bindsLocalPreferences(pref: PreferencesLoginLegacy): LocalPreferences

	@ContributesAndroidInjector
	abstract fun contributeSignInActivity(): LoginActivityLegacy

	@ContributesAndroidInjector
	abstract fun contributeSignInFragment(): LoginFragmentLegacy

	@Binds
	@IntoMap
	@ViewModelKeyLegacy(LoginViewModelLegacy::class)
	abstract fun bindSignInViewModel(signInViewModel: LoginViewModelLegacy): ViewModel
}