package cz.kotox.routines

import android.content.Context
import android.content.SharedPreferences
import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon
import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon.Companion.PREFS_DEFAULT_VALUE
import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon.Companion.PREFS_JWT_TOKEN
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class PreferencesCoreUnitTest {

	companion object {
		const val TOKEN_VALUE = "tokenValue"
	}

	@Mock
	lateinit var context: Context

	@Mock
	lateinit var sharedPreferences: SharedPreferences

	@Mock
	lateinit var editor: SharedPreferences.Editor

	lateinit var preferencesCore: PreferencesCommon

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)
		preferencesCore = Mockito.spy(PreferencesCommon(context, sharedPreferences))
	}

	@Test
	fun `expect token value when request sampleToken`() {
		Mockito.doReturn(TOKEN_VALUE).`when`(sharedPreferences).getString(PREFS_JWT_TOKEN, PREFS_DEFAULT_VALUE)
		Assert.assertEquals(TOKEN_VALUE, preferencesCore.sampleToken)
	}

	class SampleTokenNullException : RuntimeException()

	@Test(expected = SampleTokenNullException::class)
	fun `expect clearSampletoken put null to sampleToken value`() {
		doReturn(editor).`when`(sharedPreferences).edit()
		Mockito.`when`(sharedPreferences.edit().putString(PREFS_JWT_TOKEN, null)).thenThrow(SampleTokenNullException())
		preferencesCore.clearJwtToken()
	}
}