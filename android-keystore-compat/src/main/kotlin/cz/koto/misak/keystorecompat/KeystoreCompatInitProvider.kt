package cz.koto.misak.keystorecompat

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri


/**
 * Content provider able to initialize library with the context of hosted application without needs to initialize library manually.
 * The only pre-condition is, that hosted application has applicationId defined.
 * When missing this pre-condition IllegalStateException is thrown.
 */
class KeystoreCompatInitProvider : ContentProvider() {

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null
    }

    override fun onCreate(): Boolean {
        val context = context
        KeystoreCompat.init(context)
        return true
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String? {
        return null
    }

    override fun attachInfo(context: Context, providerInfo: ProviderInfo?) {
        if (providerInfo == null) {
            throw NullPointerException("KeystoreCompatInitProvider ProviderInfo cannot be null.")
        }
        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        if ("cz.koto.misak.keystorecompat.KeystoreCompatInitProvider" == providerInfo.authority) {
            throw IllegalStateException("Incorrect provider authority in manifest. Most likely due to a " + "missing applicationId variable in application\'s build.gradle.")
        }
        super.attachInfo(context, providerInfo)
    }
}
