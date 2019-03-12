package cz.kotox.securityshowcase.loginx.ui

import android.os.Bundle
import cz.kotox.securityshowcase.core.arch.BaseActivity

class SplashActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

//		1) zjisti z preferenci, jeslti je userId
//		2) ANO -> zkus dotaz na server (stahni user identity) - mozna zacni fejkem!!!!
//			2A) SUCCESS -> smeruj na main screenu
//			2B) FAILURE -> smeruj na Login screenu
//		3) NE -> smeruj na Login screenu
//		4) finish() vzdy

	}

}