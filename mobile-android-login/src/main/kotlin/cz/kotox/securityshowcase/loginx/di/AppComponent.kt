package cz.kotox.securityshowcase.loginx.di

import android.app.Application
import cz.kotox.securityshowcase.core.ApplicationInterfaceContract
import cz.kotox.securityshowcase.core.di.AppInitAction
import cz.kotox.securityshowcase.core.di.BaseDaggerModule
import cz.kotox.securityshowcase.core.di.FeatureCoreModule
import cz.kotox.securityshowcase.core.entity.AppVersion
import cz.kotox.securityshowcase.loginx.SecurityShowcaseLoginApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	AndroidInjectionModule::class,
	AndroidSupportInjectionModule::class,
	FeatureCoreModule::class,
	ViewModelModule::class,
	BaseDaggerModule::class,
	AppOnScreenNavDaggerModule::class
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

	fun inject(appOnScreenNavApplication: SecurityShowcaseLoginApplication)

	@get:AppInitAction
	val initActions: Set<() -> Unit>


}