package cz.koto.securityshowcase.app_legacylogin.di

import android.app.Application
import cz.koto.securityshowcase.app_legacylogin.SecurityShowcaseLoginApplicationLegacy
import cz.koto.securityshowcase.core.ApplicationInterfaceContract
import cz.koto.securityshowcase.core.di.AppInitAction
import cz.koto.securityshowcase.core.di.CoreDaggerModule
import cz.koto.securityshowcase.core.entity.AppVersionLegacy
import cz.koto.securityshowcase.login.di.LoginDaggerModuleLegacy
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	AndroidInjectionModule::class,
	AndroidSupportInjectionModule::class,
	ViewModelModuleLegacy::class,
	CoreDaggerModule::class,
	LoginDaggerModuleLegacy::class,
	AppLoginDaggerModuleLegacy::class
])
interface AppComponentLegacy {
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: Application): Builder

		@BindsInstance
		fun appVersion(appVersion: AppVersionLegacy): Builder

		@BindsInstance
		fun applicationInterface(appInterface: ApplicationInterfaceContract): Builder

		fun build(): AppComponentLegacy
	}

	fun inject(loginApplication: SecurityShowcaseLoginApplicationLegacy)

	@get:AppInitAction
	val initActions: Set<() -> Unit>

}