package cz.kotox.securityshowcase.core.crypto.hash

import dagger.Module
import dagger.Provides
import java.security.MessageDigest

@Module
object HashDaggerModule {

	@Provides
	@JvmStatic
	@Sha512
	fun sha512(input: String): String = hashString("SHA-512", input)

	@Provides
	@JvmStatic
	@Sha256
	fun sha256(input: String): String = hashString("SHA-256", input)

	@Provides
	@JvmStatic
	@Sha1
	fun sha1(input: String): String = hashString("SHA-1", input)

	/**
	 * Supported algorithms on Android:
	 *
	 * Algorithm	Supported API Levels
	 * MD5          1+
	 * SHA-1	    1+
	 * SHA-224	    1-8,22+
	 * SHA-256	    1+
	 * SHA-384	    1+
	 * SHA-512	    1+
	 */
	private fun hashString(type: String, input: String): String {
		val hexChars = "0123456789ABCDEF"
		val bytes = MessageDigest
			.getInstance(type)
			.digest(input.toByteArray())
		val result = StringBuilder(bytes.size * 2)

		bytes.forEach {
			val i = it.toInt()
			result.append(hexChars[i shr 4 and 0x0f])
			result.append(hexChars[i and 0x0f])
		}

		return result.toString()
	}

}
