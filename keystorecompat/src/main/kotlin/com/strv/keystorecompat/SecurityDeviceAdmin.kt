package com.strv.keystorecompat

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

/**
 * Prepared to support LOCK screen on pre-lollipop versions.
 */
class SecurityDeviceAdmin : DeviceAdminReceiver() {

    companion object {
        val INSTANCE by lazy { SecurityDeviceAdmin() }
    }

    var mDPM: DevicePolicyManager? = null
    var mAdminName: ComponentName? = null

    fun init() {
        mDPM = KeystoreCompat.context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        mAdminName = ComponentName(KeystoreCompat.context, SecurityDeviceAdmin::class.java)
    }

    /**
     *
     * https://rootfs.wordpress.com/2010/09/09/android-make-your-application-a-device-administrator/
     */
    fun forceLockPreLollipop(onPermanentFailure: (Exception) -> Unit) {
        if (mDPM == null) return
        if (!mDPM!!.isAdminActive(mAdminName)) {//try become active
            var intent: Intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName)

            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
            onPermanentFailure(RuntimeException("TBD.XY")) //activity.startActivityForResult(intent, CredentialsKeystoreProvider.FORCE_SIGNUP_REQUEST);
        } else {//already a device administrator, can do security opertations now
            mDPM!!.lockNow();
        }
    }

    override fun onEnabled(context: Context?, intent: Intent?) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context?, intent: Intent?) {
        super.onDisabled(context, intent)
    }
}
