package cz.kotox.securityshowcase.security.di

import android.content.Context
import android.os.Build
import cz.kotox.keystorecompat.BuildConfig
import cz.kotox.keystorecompat.KeystoreCompat
import cz.kotox.keystorecompat.compat.KeystoreCompatConfig
import cz.kotox.securityshowcase.security.entity.CredentialStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object KeystoreCompatModule {

	@Provides
	@JvmStatic
	@Singleton
	fun provideKeystoreCompatConfig(): KeystoreCompatConfig = ShowcaseKeystoreCompatConfig()

	@Provides
	@JvmStatic
	@Singleton
	fun provideKeystoreCompat(context: Context, config: KeystoreCompatConfig): KeystoreCompat = KeystoreCompat.getInstance(context, config)

	@Provides
	@JvmStatic
	@Singleton
	fun provideCredentialStorage() = CredentialStorage()
}

class ShowcaseKeystoreCompatConfig : KeystoreCompatConfig() {

	/**
	 * How many cancellation is necessary to suppress AndroidLoginScreen / KitkatAdminRequestDialog .
	 */
	override fun getDialogDismissThreshold(): Int {
		return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			1 //In case of Admin request dialog on KitKat escape after first cancel click.
		} else {
			2 //In case of standard Android security dialog dismiss dialog after second CANCEL button click
		}
	}

	override fun isRootDetectionEnabled(): Boolean {
		return if (BuildConfig.DEBUG) false else super.isRootDetectionEnabled()
	}
}
