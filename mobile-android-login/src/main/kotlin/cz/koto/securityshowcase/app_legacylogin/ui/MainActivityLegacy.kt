package cz.koto.securityshowcase.app_legacylogin.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import cz.koto.securityshowcase.R
import cz.koto.securityshowcase.module_core.arch.BaseActivity
import cz.koto.securityshowcase.module_core.database.preferences.PreferencesCommon
import timber.log.Timber
import javax.inject.Inject

class MainActivityLegacy : BaseActivity() {

	@Inject
	lateinit var preferencesCore: PreferencesCommon

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity_legacy)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		val host: NavHostFragment = supportFragmentManager
			.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

		// Set up Action Bar
		val navController = host.navController
		setupActionBar(navController)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			val dest: String = try {
				resources.getResourceName(destination.id)
			} catch (e: Throwable) {
				Integer.toString(destination.id)
			}

			Timber.d("Navigated to %s", dest)
		}

		//TODO Timber.d(">>>${preferencesCore.sampleToken}")
	}

	private fun setupActionBar(navController: NavController) {
		NavigationUI.setupActionBarWithNavController(this, navController)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val retValue = super.onCreateOptionsMenu(menu)
		menuInflater.inflate(R.menu.menu_overflow, menu)
		return retValue
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Have the NavHelper look for an action or destination matching the menu
		// item id and navigate there if found.
		// Otherwise, bubble up to the parent.
		return NavigationUI.onNavDestinationSelected(item,
			Navigation.findNavController(this, R.id.my_nav_host_fragment))
			|| super.onOptionsItemSelected(item)
	}

}