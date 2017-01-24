package cz.koto.misak.securityshowcase.storage

import com.strv.keystorecompat.KeystoreCompat
import cz.koto.misak.securityshowcase.model.AuthResponseSimple
import cz.koto.misak.securityshowcase.utility.Logcat
import cz.koto.misak.securityshowcase.utility.runOnLollipop


object CredentialStorage {

    private var accessToken: String? = null
    private var userName: String? = null
    private var password: String? = null

    fun getAccessToken(): String? {
        if (accessToken != null)
            Logcat.d("getToken %s", accessToken!!)
        else
            Logcat.d("NULL token!")
        return accessToken
    }

    fun getUserName() = userName
    fun getPassword() = password

    fun storeUser(authResponse: AuthResponseSimple?, username: String, pass: String) =
            authResponse?.let {
                if (it.successful) {
                    accessToken = "Token " + authResponse.token
                    userName = username
                    password = pass
                }
            }

    fun performLogout() {
        accessToken = null
        userName = null
        password = null
        runOnLollipop { KeystoreCompat.forceTypeCredentials = true }
    }

}