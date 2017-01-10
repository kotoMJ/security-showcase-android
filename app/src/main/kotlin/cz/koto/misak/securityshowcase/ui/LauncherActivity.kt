package cz.koto.misak.securityshowcase.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.koto.misak.securityshowcase.SecurityConfig
import cz.koto.misak.securityshowcase.storage.CredentialStorage
import cz.koto.misak.securityshowcase.ui.login.LoginActivity
import cz.koto.misak.securityshowcase.ui.main.MainActivity
import cz.koto.misak.securityshowcase.utility.longPref
import cz.koto.misak.securityshowcase.utility.start


class LauncherActivity : AppCompatActivity() {

    val loginAt by longPref("last_login_at")
    // assuming token expiration period is set for 30 - 60 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val now = System.nanoTime()

        if (SecurityConfig.isPackageDebug()) {
            CredentialStorage.getAccessToken()?.let {
                if (loginAt + halfAnHourInNano >= now)
                    start<MainActivity>()
                else
                    start<LoginActivity>()
            } ?: start<LoginActivity>()
        } else {
            start<LoginActivity>()
        }

        finish()
    }
}

val halfAnHourInNano = 1800000000000L