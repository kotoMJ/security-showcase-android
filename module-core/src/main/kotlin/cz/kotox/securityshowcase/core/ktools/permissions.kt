package cz.kotox.securityshowcase.core.ktools

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class PermissionManager {
	private var lastRequestId = 0
	private val permissionRequests = HashMap<Int, PermissionRequest>()
	fun requestPermission(permissionRequest: PermissionRequest) {
		val requestId = ++lastRequestId
		permissionRequests[requestId] = permissionRequest
		askForPermissions(permissionRequest.permissions, requestId)
	}

	fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		val permissionRequest = permissionRequests[requestCode]
		permissionRequest?.let {
			val granted = ArrayList<String>()
			val denied = ArrayList<String>()

			permissions.indices.forEach {
				if (grantResults[it] == PackageManager.PERMISSION_GRANTED)
					granted.add(permissions[it])
				else
					denied.add(permissions[it])
			}


			if (granted.isNotEmpty())
				permissionRequest.grantedCallback.invoke(granted)
			if (denied.isNotEmpty())
				permissionRequest.deniedCallback.invoke(denied)

			permissionRequests.remove(requestCode)
		}
	}

	fun checkPermission(permission: String) = performPermissionCheck(permission) == PackageManager.PERMISSION_GRANTED

	abstract fun performPermissionCheck(permission: String): Int

	abstract fun askForPermissions(permissions: List<String>, requestId: Int)
}

class ActivityPermissionManager(val activity: FragmentActivity) : PermissionManager() {
	override fun performPermissionCheck(permission: String) = ContextCompat.checkSelfPermission(activity, permission)

	override fun askForPermissions(permissions: List<String>, requestId: Int) {
		ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestId)
	}
}

class FragmentPermissionManager(val fragment: Fragment) : PermissionManager() {
	override fun performPermissionCheck(permission: String) = ContextCompat.checkSelfPermission(fragment.activity!!, permission)

	override fun askForPermissions(permissions: List<String>, requestId: Int) {
		fragment.requestPermissions(permissions.toTypedArray(), requestId)
	}
}

open class PermissionRequest(
	val permissions: List<String>,
	val grantedCallback: (grantedPermissions: List<String>) -> Unit = {},
	val deniedCallback: (deniedPermissions: List<String>) -> Unit = {})

class SinglePermissionRequest(
	permission: String,
	grantedCallback: (grantedPermission: String) -> Unit = {},
	deniedCallback: (deniedPermission: String) -> Unit = {}) : PermissionRequest(listOf(permission), { grantedCallback(it[0]) }, { deniedCallback(it[0]) })