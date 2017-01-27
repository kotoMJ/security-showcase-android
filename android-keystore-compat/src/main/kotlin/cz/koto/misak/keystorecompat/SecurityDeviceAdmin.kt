package cz.koto.misak.keystorecompat

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Prepared to support LOCK screen on pre-lollipop versions.
 */
class SecurityDeviceAdmin : DeviceAdminReceiver {

    var mDPM: DevicePolicyManager = KeystoreCompat.context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    var mAdminName: ComponentName = ComponentName(KeystoreCompat.context, SecurityDeviceAdmin::class.java)

    constructor() {
    }

    companion object {
        val INSTANCE by lazy { SecurityDeviceAdmin() }
    }


    /**
     *
     * https://rootfs.wordpress.com/2010/09/09/android-make-your-application-a-device-administrator/
     */
    fun forceLockPreLollipop(onLockActivityShouldBeInvoked: (Intent) -> Unit, onSuccess: () -> Unit) = if (!mDPM.isAdminActive(mAdminName)) {
        //try become active
        val intent: Intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, KeystoreCompat.context.getString(R.string.kc_kitkat_admin_explanatory));
        onLockActivityShouldBeInvoked.invoke(intent)
    } else {
        //already a device administrator, can do security opertations now
        mDPM.lockNow(); //TODO lock works, but with strange black screen, needs to be tuned additionally
        onSuccess.invoke()
    }


    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        super.onDisabled(context, intent)
    }
}
