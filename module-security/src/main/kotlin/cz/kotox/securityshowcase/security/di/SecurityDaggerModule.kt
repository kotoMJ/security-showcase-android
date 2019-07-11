package cz.kotox.securityshowcase.security.di

import androidx.lifecycle.ViewModel
import cz.kotox.securityshowcase.core.di.ViewModelKey
import cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment.SettingsFragment
import cz.kotox.securityshowcase.security.ui.keystorecompat.enrollment.SettingsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [KeystoreCompatModule::class])
abstract class SecurityDaggerModule {

	@ContributesAndroidInjector
	abstract fun contributeSignInFragment(): SettingsFragment

	@Binds
	@IntoMap
	@ViewModelKey(SettingsViewModel::class)
	abstract fun bindSignInViewModel(signInViewModel: SettingsViewModel): ViewModel

}
