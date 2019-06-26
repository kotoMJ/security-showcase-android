package cz.kotox.securityshowcase.module_core.di

import javax.inject.Qualifier

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Qualifier
annotation class AppInitAction
