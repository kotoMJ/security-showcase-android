package cz.kotox.securityshowcase.login.qualifier

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TestCredentials

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TestAuthEmail

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TestAuthPassword
