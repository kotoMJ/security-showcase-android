package cz.kotox.keystorecompat19

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

	/**
	 *
	 * https://rootfs.wordpress.com/2010/09/09/android-make-your-application-a-device-administrator/
	 */
	fun forceLockPreLollipop(
		context: Context,
		onLockActivityShouldBeInvoked: (Intent) -> Unit,
		onSuccess: () -> Unit) = if (!getDevicePolicyManager(context).isAdminActive(getAdminName(context))
	) {
		//try become active
		val intent: Intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, getAdminName(context))
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.kc_kitkat_admin_explanatory));
		onLockActivityShouldBeInvoked.invoke(intent)
	} else {
		//already a device administrator, can do security opertations now
		getDevicePolicyManager(context).lockNow();
		onSuccess.invoke()
	}

	fun deactivateDeviceAdmin(context: Context) {
		val adminName: ComponentName = getAdminName(context)
		val mDPM: DevicePolicyManager = getDevicePolicyManager(context)
		if (mDPM.isAdminActive(adminName)) {
			mDPM.removeActiveAdmin(adminName);
		} else {
			Log.e("SecurityDeviceAdmin", "DeactivateDeviceAdmin called for not active device admin. No action took place.")
		}
	}

	private fun getDevicePolicyManager(context: Context): DevicePolicyManager {
		return context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
	}

	private fun getAdminName(context: Context): ComponentName = ComponentName(context, SecurityDeviceAdmin::class.java)

	override fun onEnabled(context: Context?, intent: Intent?) {
		super.onEnabled(context, intent)
	}

	override fun onDisabled(context: Context?, intent: Intent?) {
		super.onDisabled(context, intent)
	}
}
