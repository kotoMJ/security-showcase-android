package cz.kotox.securityshowcase.login.di

import cz.kotox.securityshowcase.login.qualifier.TestAuthEmail
import cz.kotox.securityshowcase.login.qualifier.TestAuthPassword
import dagger.Module
import dagger.Provides

@Module
object CredentialsDaggerModule {

	@Provides
	@JvmStatic
	@TestAuthEmail
	fun provideAuthEmail(): String = "security@showcase.cz"

	@Provides
	@JvmStatic
	@TestAuthPassword
	fun provideAuthPassword(): String = "showcase1234"
}