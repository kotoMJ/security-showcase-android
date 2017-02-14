package cz.koto.misak.keystorecompat

import android.content.Context
import java.security.KeyStore
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.security.auth.x500.X500Principal

internal interface KeystoreCompatFacade {

    fun getAlgorithm(): String

    fun getCipherMode(): String

    fun storeSecret(secret: ByteArray, privateKeyEntry: KeyStore.Entry, useBase64Encoding: Boolean): String

    fun loadSecret(onSuccess: (cre: ByteArray) -> Unit,
                   onFailure: (e: Exception) -> Unit,
                   clearCredentials: () -> Unit,
                   forceFlag: Boolean?,
                   encryptedUserData: String,
                   privateKeyEntry: KeyStore.Entry,
                   isBase64Encoded: Boolean)

    fun getAlgorithmParameterSpec(certSubject: X500Principal, alias: String, startDate: Date, endDate: Date, context: Context): AlgorithmParameterSpec

    fun isSecurityEnabled(context: Context): Boolean

    fun generateKeyPair(alias: String, start: Date, end: Date, certSubject: X500Principal, context: Context)

}