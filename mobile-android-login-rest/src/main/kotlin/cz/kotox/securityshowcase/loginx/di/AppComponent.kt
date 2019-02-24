package cz.kotox.securityshowcase.loginx.di

import android.app.Application
import cz.kotox.securityshowcase.core.di.FeatureCoreModule
import cz.kotox.securityshowcase.loginx.SecurityShowcaseLoginRest
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
	AppOnScreenNavDaggerModule::class
])
interface AppComponent {
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: Application): Builder

		fun build(): AppComponent
	}

	fun inject(appOnScreenNavApplication: SecurityShowcaseLoginRest)

}