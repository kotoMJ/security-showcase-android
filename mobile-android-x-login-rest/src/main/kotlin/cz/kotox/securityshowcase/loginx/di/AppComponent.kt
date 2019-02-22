package cz.kotox.routines.di

import android.app.Application
import cz.kotox.routines.AppOnScreenNavApplication
import cz.kotox.securityshowcase.core.di.FeatureCoreModule
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

	fun inject(appOnScreenNavApplication: AppOnScreenNavApplication)

}