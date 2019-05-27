package cz.kotox.securityshowcase.login.biometric.di

import android.app.Application
import cz.kotox.securityshowcase.core.ApplicationInterfaceContract
import cz.kotox.securityshowcase.core.di.AppInitAction
import cz.kotox.securityshowcase.core.di.CoreDaggerModule
import cz.kotox.securityshowcase.core.entity.AppVersion
import cz.kotox.securityshowcase.login.biometric.SecurityShowcaseLoginBiometricApplication
import cz.kotox.securityshowcase.login.di.LoginDaggerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	AndroidInjectionModule::class,
	AndroidSupportInjectionModule::class,
	ViewModelModule::class,
	CoreDaggerModule::class,
	LoginDaggerModule::class,
	AppLoginDaggerModule::class
])
interface AppComponent {
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: Application): Builder

		@BindsInstance
		fun appVersion(appVersion: AppVersion): Builder

		@BindsInstance
		fun applicationInterface(appInterface: ApplicationInterfaceContract): Builder

		fun build(): AppComponent
	}

	fun inject(loginApplication: SecurityShowcaseLoginBiometricApplication)

	@get:AppInitAction
	val initActions: Set<() -> Unit>

}