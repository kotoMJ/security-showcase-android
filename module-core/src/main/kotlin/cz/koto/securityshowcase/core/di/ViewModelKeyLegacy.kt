package cz.kotox.securityshowcase.module_core.di

import dagger.MapKey
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKeyLegacy(val value: KClass<out ViewModel>)