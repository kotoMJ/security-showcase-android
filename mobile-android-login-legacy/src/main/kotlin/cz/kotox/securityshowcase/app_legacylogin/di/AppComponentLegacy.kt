package cz.kotox.securityshowcase.app_legacylogin.di

import android.app.Application
import cz.kotox.securityshowcase.app_legacylogin.SecurityShowcaseLoginApplicationLegacy
import cz.kotox.securityshowcase.module_core.ApplicationInterfaceContract
import cz.kotox.securityshowcase.module_core.di.AppInitAction
import cz.kotox.securityshowcase.module_core.di.CoreDaggerModule
import cz.kotox.securityshowcase.module_core.entity.AppVersionLegacy
import cz.kotox.securityshowcase.module_login.di.LoginDaggerModuleLegacy
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