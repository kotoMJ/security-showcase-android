package cz.kotox.keystorecompat.base.utility

import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Build

fun showLockScreenSettings(context: Context) {
	val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
	intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
	context.startActivity(intent)
}

inline fun forceAndroidAuth(title: String, desc: String, onIntentReady: (intent: Intent) -> Unit, context: Context) {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		val km: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		val intent = km.createConfirmDeviceCredentialIntent(title, desc)
		if (intent != null) {
			onIntentReady.invoke(intent)
		}
	}
}
