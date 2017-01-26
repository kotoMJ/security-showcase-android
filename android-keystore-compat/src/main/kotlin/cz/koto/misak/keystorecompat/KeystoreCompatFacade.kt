package cz.koto.misak.keystorecompat

import android.content.Context
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

internal interface KeystoreCompatFacade {

    fun loadCredentials(onSuccess: (cre: String) -> Unit,
                        onFailure: (e: Exception) -> Unit,
                        clearCredentials: () -> Unit,
                        forceFlag: Boolean?,
                        encryptedUserData: String,
                        privateKeyEntry: KeyStore.PrivateKeyEntry)

    fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec

    fun isSecurityEnabled(context: Context): Boolean
}