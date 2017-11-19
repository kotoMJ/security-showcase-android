package cz.koto.keystorecompat.emplus

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import cz.koto.keystorecompat.base.KeystoreCompatBase
import cz.koto.keystorecompat.base.SingletonHolder
import cz.koto.keystorecompat.base.compat.KeystoreCompatFacade
import cz.koto.keystorecompat.base.utility.PrefDelegate
import cz.koto.keystorecompat.base.utility.runSinceMarshmallow
import cz.koto.keystorecompat.emplus.compat.KeystoreCompatConfig
import cz.koto.keystorecompat.emplus.compat.KeystoreCompatImpl
import java.security.KeyStore
import javax.security.auth.x500.X500Principal


/**
 * The Keystore itself is encrypted using (not only) the user’s own lockScreen pin/password,
 * hence, when the device screen is locked the Keystore is unavailable.
 * Keep this in mind if you have a background service that could need to access your application secrets.
 *
 * With KeyStoreProvider each app can only access to their KeyStore instances or aliases!
 *
 */
@TargetApi(Build.VERSION_CODES.M)
class KeystoreCompat private constructor(override val context: Context, override val config: KeystoreCompatConfig = KeystoreCompatConfig()) : KeystoreCompatBase(config, context) {

	companion object : SingletonHolder<KeystoreCompat, Context, KeystoreCompatConfig>(::KeystoreCompat)

	init {

		runSinceMarshmallow {
			this.uniqueId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
			Log.d(LOG_TAG, "uniqueId:${uniqueId}")
			PrefDelegate.initialize(this.context)
			certSubject = X500Principal("CN=$uniqueId, O=Android Authority")

			keyStore = KeyStore.getInstance(KeystoreCompatFacade.KEYSTORE_KEYWORD)
			keyStore.load(null)
			keystoreCompatImpl = KeystoreCompatImpl(config).apply { init(Build.VERSION.SDK_INT) }.keystoreCompat
		}
	}

}

