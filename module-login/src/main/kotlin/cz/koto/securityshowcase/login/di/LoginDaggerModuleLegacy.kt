package cz.koto.securityshowcase.login.di

import android.arch.lifecycle.ViewModel
import cz.koto.securityshowcase.core.database.preferences.LocalPreferences
import cz.koto.securityshowcase.core.di.ViewModelKeyLegacy
import cz.koto.securityshowcase.login.LoginActivityLegacy
import cz.koto.securityshowcase.login.LoginFragmentLegacy
import cz.koto.securityshowcase.login.database.preferences.PreferencesLoginLegacy
import cz.koto.securityshowcase.module_login.LoginViewModelLegacy
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