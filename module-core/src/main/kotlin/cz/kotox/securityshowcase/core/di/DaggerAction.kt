package cz.kotox.securityshowcase.core.di

import javax.inject.Qualifier

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Qualifier
annotation class AppInitAction
