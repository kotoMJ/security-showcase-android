package cz.kotox.securityshowcase.module_core.ktools

/**
 * Shortcut for lazy with no thread safety
 */
fun <T> lazyUnsafe(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)