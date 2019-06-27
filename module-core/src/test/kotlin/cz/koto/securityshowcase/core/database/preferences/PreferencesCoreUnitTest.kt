package cz.koto.securityshowcase.core.database.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesCoreUnitTest {

	@Mock
	lateinit var context: Context

	@Mock
	lateinit var sharedPreferences: SharedPreferences

	lateinit var preferencesCommon: PreferencesCommon

	@Before
	fun setup() {
		MockitoAnnotations.initMocks(this)

		preferencesCommon = Mockito.spy(PreferencesCommon(context, sharedPreferences))

		doReturn("string").`when`(context).getString(ArgumentMatchers.anyInt())
		doNothing().`when`(preferencesCommon).clearJwtToken()
		doNothing().`when`(preferencesCommon).clearUserId()
	}

	@Test
	fun `ensure cleanForSignOut calls proper method`() {
		preferencesCommon.clearForSignOut()
		verify(preferencesCommon).clearJwtToken()
		verify(preferencesCommon).clearUserId()
	}
}