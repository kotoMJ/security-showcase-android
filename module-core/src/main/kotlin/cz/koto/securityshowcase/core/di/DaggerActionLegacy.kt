package cz.koto.securityshowcase.core.di

import javax.inject.Qualifier

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Qualifier
annotation class AppInitAction
