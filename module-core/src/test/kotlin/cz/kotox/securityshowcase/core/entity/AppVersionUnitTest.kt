package cz.kotox.securityshowcase.core.entity

import org.junit.Assert
import org.junit.Test

class AppVersionUnitTest {

	@Test
	fun `expect deep copy of the AppVersion will keep all attributes`() {

		val actualAppVersion = AppVersion(versionCode = 1, versionName = "one")

		val expectedAppVersion = actualAppVersion.copy()

		Assert.assertEquals(expectedAppVersion, actualAppVersion)
		Assert.assertEquals(expectedAppVersion.versionCode, actualAppVersion.versionCode)
		Assert.assertEquals(expectedAppVersion.versionName, actualAppVersion.versionName)

		Assert.assertEquals(expectedAppVersion.versionCode, expectedAppVersion.component1())
		Assert.assertEquals(expectedAppVersion.versionName, expectedAppVersion.component2())

		Assert.assertEquals(expectedAppVersion.hashCode(), actualAppVersion.hashCode())
		Assert.assertEquals(expectedAppVersion.toString(), actualAppVersion.toString())
	}

}