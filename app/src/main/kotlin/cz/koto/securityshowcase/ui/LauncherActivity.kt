package cz.koto.securityshowcase.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.koto.securityshowcase.ui.login.LoginActivity
import cz.koto.securityshowcase.utility.start


class LauncherActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		start<LoginActivity>()
		finish()
	}
}