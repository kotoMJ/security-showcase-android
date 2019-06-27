package cz.koto.securityshowcase.app_legacylogin.ui

import android.os.Bundle
import cz.koto.securityshowcase.module_core.arch.BaseActivity
import cz.koto.securityshowcase.module_login.LoginActivityIntent

class SplashActivityLegacy : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

//		1) zjisti z preferenci, jeslti je userId
//		2) ANO -> zkus dotaz na server (stahni user identity) - mozna zacni fejkem!!!!
//			2A) SUCCESS -> smeruj na main screenu
//			2B) FAILURE -> smeruj na Login screenu
//		3) NE -> smeruj na Login screenu
//		4) finish() vzdy

		startActivity(LoginActivityIntent())

	}

}