package cz.kotox.securityshowcase.login.ui.biometric

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cz.kotox.securityshowcase.core.arch.BaseActivity
import cz.kotox.securityshowcase.core.database.preferences.PreferencesCommon
import cz.kotox.securityshowcase.login.R
import timber.log.Timber
import javax.inject.Inject

fun Context.loginBiometricActivityIntent(/*user: User*/): Intent {
	val loginIntent = Intent(this, LoginBiometricActivity::class.java).apply {
		//putExtra(INTENT_USER_ID, user.id)
	}
	loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
	return loginIntent
}

//private const val INTENT_USER_ID = "user_id"

class LoginBiometricActivity : BaseActivity() {

	@Inject
	lateinit var preferencesCore: PreferencesCommon

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		//val userId = intent.getStringExtra(INTENT_USER_ID)
		//requireNotNull(userId){"no user_id provided in Intent extras"}

		setContentView(R.layout.activity_login)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		val host: NavHostFragment = supportFragmentManager
			.findFragmentById(R.id.login_nav_host_fragment) as NavHostFragment? ?: return

		// Set up Action Bar
		val navController = host.navController
		setupActionBar(navController)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			val dest: String = try {
				resources.getResourceName(destination.id)
			} catch (e: Resources.NotFoundException) {
				Integer.toString(destination.id)
			}

			Timber.d("Navigated to %s", dest)
		}

		Timber.d(">>>${preferencesCore.jwtToken}")
	}

	private fun setupActionBar(navController: NavController) {
		NavigationUI.setupActionBarWithNavController(this, navController)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Have the NavHelper look for an action or destination matching the menu
		// item id and navigate there if found.
		// Otherwise, bubble up to the parent.
		return NavigationUI.onNavDestinationSelected(item,
			Navigation.findNavController(this, R.id.login_nav_host_fragment))
			|| super.onOptionsItemSelected(item)
	}

}
