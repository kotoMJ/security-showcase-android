package cz.koto.keystorecompat_base


/**
 * Trust the Kotlin authors on this: this code is actually borrowed directly
 * from the implementation of the lazy() function in the Kotlin standard library,
 * which is synchronized by default. It has been modified to allow passing an argument to the creator function.
 */
open class SingletonHolder<out T, in A, in B>(creator: (A, B) -> T) {
	private var creator: ((A, B) -> T)? = creator
	@Volatile private var instance: T? = null

	fun getInstance(arg1: A, arg2: B): T {
		val i = instance
		if (i != null) {
			return i
		}

		return synchronized(this) {
			val i2 = instance
			if (i2 != null) {
				i2
			} else {
				val created = creator!!(arg1, arg2)
				instance = created
				creator = null
				created
			}
		}
	}
}