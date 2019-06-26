package cz.kotox.securityshowcase.login.di

import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.database.preferences.LocalPreferences
import cz.kotox.securityshowcase.core.di.ViewModelKey
import cz.kotox.securityshowcase.login.database.preference.PreferencesLogin
import cz.kotox.securityshowcase.login.ui.biometric.LoginBiometricActivity
import cz.kotox.securityshowcase.login.ui.biometric.LoginBiometricFragment
import cz.kotox.securityshowcase.login.ui.biometric.LoginBiometricViewModel
import cz.kotox.securityshowcase.login.ui.keystorecompat.LoginCompatActivity
import cz.kotox.securityshowcase.login.ui.keystorecompat.LoginCompatFragment
import cz.kotox.securityshowcase.login.ui.keystorecompat.LoginCompatViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet

@Module(includes = [
	CredentialsDaggerModule::class
])
abstract class LoginDaggerModule {
	@Binds
	@IntoSet
	abstract fun bindsLocalPreferences(pref: PreferencesLogin): LocalPreferences

	@ContributesAndroidInjector
	abstract fun contributeSignInActivity(): LoginBiometricActivity

	@ContributesAndroidInjector
	abstract fun contributeSignInFragment(): LoginBiometricFragment

	@Binds
	@IntoMap
	@ViewModelKey(LoginBiometricViewModel::class)
	abstract fun bindSignInViewModel(signInViewModel: LoginBiometricViewModel): ViewModel

	@ContributesAndroidInjector
	abstract fun contributeSignInCompatActivity(): LoginCompatActivity

	@ContributesAndroidInjector
	abstract fun contributeSignInCompatFragment(): LoginCompatFragment

	@Binds
	@IntoMap
	@ViewModelKey(LoginCompatViewModel::class)
	abstract fun bindSignInCompatViewModel(signInViewModel: LoginCompatViewModel): ViewModel
}