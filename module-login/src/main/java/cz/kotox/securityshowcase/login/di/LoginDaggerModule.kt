package cz.kotox.securityshowcase.login.di

import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.database.preferences.LocalPreferences
import cz.kotox.securityshowcase.core.di.ViewModelKey
import cz.kotox.securityshowcase.login.LoginActivity
import cz.kotox.securityshowcase.login.LoginFragment
import cz.kotox.securityshowcase.login.LoginViewModel
import cz.kotox.securityshowcase.login.database.preference.PreferencesLogin
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet

@Module
abstract class LoginDaggerModule {
	@Binds
	@IntoSet
	abstract fun bindsLocalPreferences(pref: PreferencesLogin): LocalPreferences

	@ContributesAndroidInjector
	abstract fun contributeSignInActivity(): LoginActivity

	@ContributesAndroidInjector
	abstract fun contributeSignInFragment(): LoginFragment

	@Binds
	@IntoMap
	@ViewModelKey(LoginViewModel::class)
	abstract fun bindSignInViewModel(signInViewModel: LoginViewModel): ViewModel
}