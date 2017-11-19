package cz.koto.keystorecompat.base

import android.content.Context
import android.os.Build
import android.util.Log
import com.scottyab.rootbeer.RootBeer
import com.scottyab.rootbeer.util.Utils


abstract class KeystoreCompatBase(open val config: KeystoreCompatConfigBase) {

	private var isRooted: Boolean? = null
	private val LOG_TAG = javaClass.name

	protected fun isDeviceRooted(context: Context): Boolean {
		val ret = RootBeer(context).isRooted

		if (this.isRooted == null) {
			if (ret) {
				val check: RootBeer = RootBeer(context)
				Log.w(LOG_TAG, "RootDetection enabled ${config.isRootDetectionEnabled()}")
				Log.w(LOG_TAG, "Root Management Apps ${if (check.detectRootManagementApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "PotentiallyDangerousApps ${if (check.detectPotentiallyDangerousApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "TestKeys ${if (check.detectTestKeys()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "BusyBoxBinary ${if (check.checkForBusyBoxBinary()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "SU Binary ${if (check.checkForSuBinary()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "2nd SU Binary check ${if (check.checkSuExists()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "ForRWPaths ${if (check.checkForRWPaths()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "DangerousProps ${if (check.checkForDangerousProps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "Root via native check ${if (check.checkForRootNative()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "RootCloakingApps ${if (check.detectRootCloakingApps()) "detected" else "not detected"}")
				Log.w(LOG_TAG, "Selinux Flag Is Enabled ${if (Utils.isSelinuxFlagInEnabled()) "true" else "false"}")
			}
			this.isRooted = ret && config.isRootDetectionEnabled()
		}

		if (this.isRooted!!) clearCredentials()

		return this.isRooted!!
	}

	abstract fun clearCredentials()

	protected fun logUnsupportedVersionForKeystore() {
		Log.w(LOG_TAG, "Device Android version[${Build.VERSION.SDK_INT}] doesn't offer trusted keystore functionality!")
	}

	abstract fun isKeystoreCompatAvailable(): Boolean

	abstract fun isSecurityEnabled(): Boolean

}
