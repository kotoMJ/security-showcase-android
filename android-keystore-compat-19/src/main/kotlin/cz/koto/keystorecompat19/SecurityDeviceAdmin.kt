package cz.koto.keystorecompat19

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * Prepared to support LOCK screen on pre-lollipop versions.
 */
class SecurityDeviceAdmin() : DeviceAdminReceiver() {

	lateinit var context: Context

	val mDPM: DevicePolicyManager by lazy {
		context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
	}
	val mAdminName: ComponentName by lazy {
		ComponentName(context, SecurityDeviceAdmin::class.java)
	}

	override fun onReceive(context: Context, intent: Intent?) {
		this.context = context
		super.onReceive(context, intent)
	}

	/**
	 *
	 * https://rootfs.wordpress.com/2010/09/09/android-make-your-application-a-device-administrator/
	 */
	fun forceLockPreLollipop(onLockActivityShouldBeInvoked: (Intent) -> Unit, onSuccess: () -> Unit) = if (!mDPM.isAdminActive(mAdminName)) {
		//try become active
		val intent: Intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.kc_kitkat_admin_explanatory));
		onLockActivityShouldBeInvoked.invoke(intent)
	} else {
		//already a device administrator, can do security opertations now
		mDPM.lockNow();
		onSuccess.invoke()
	}

	fun deactivateDeviceAdmin() {
		var mDPM: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
		if (mDPM.isAdminActive(mAdminName)) {
			mDPM.removeActiveAdmin(mAdminName);
		} else {
			Log.e("SecurityDeviceAdmin", "DeactivateDeviceAdmin called for not active device admin. No action took place.")
		}


	}

	override fun onEnabled(context: Context?, intent: Intent?) {
		super.onEnabled(context, intent)
	}

	override fun onDisabled(context: Context?, intent: Intent?) {
		super.onDisabled(context, intent)
	}
}
