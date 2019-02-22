package cz.kotox.securityshowcase.testutils

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

/**
 * Following two methods were added because it won't allow to use Mockito.any() on nullable
 * objects.
 */

fun <T> kotlinAny(): T {
	Mockito.any<T>()
	return uninitialized()
}

@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T

/**
 * a kotlin friendly mock that handles generics
 */
inline fun <reified T> mock(): T = Mockito.mock(T::class.java)

inline fun <reified T> argumentCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)