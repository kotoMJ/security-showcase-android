package cz.kotox.securityshowcase.core.di

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module(includes = [
	BaseInitModule::class
])
abstract class BaseDaggerModule {

}

@Module
object BaseInitModule {
	@Provides
	@JvmStatic
	@IntoSet
	@AppInitAction
	fun provideAppInitActions(): () -> Unit = {
		//Initialize anything related to core module.
	}
}