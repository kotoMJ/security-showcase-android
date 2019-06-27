package cz.koto.securityshowcase.core.logging

import android.support.annotation.Nullable
import android.util.Log
import timber.log.Timber

/** A tree which logs important information for crash reporting.  */
class CrashReportingTreeLegacy : Timber.Tree() {
	override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
		if (priority == Log.VERBOSE || priority == Log.DEBUG) {
			return
		}

		//TODO FakeCrashLibrary.log(priority, tag, message)

		if (t != null) {
			if (priority == Log.ERROR) {
				//TODO FakeCrashLibrary.logError(t)
			} else if (priority == Log.WARN) {
				//TODO FakeCrashLibrary.logWarning(t)
			}
		}
	}

	fun isLoggable(priority: Int, @Nullable tag: String): Boolean {
		return priority >= Log.INFO
	}
}