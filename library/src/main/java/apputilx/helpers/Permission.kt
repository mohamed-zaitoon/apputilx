package apputilx.helpers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal object Permission {

    /**
     * Check if permission is granted.
     */
    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if all permissions are granted.
     */
    fun areGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isGranted(context, it) }
    }

    /**
     * Return permissions that are not currently granted.
     */
    fun deniedPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filterNot { isGranted(context, it) }
    }

    /**
     * Check whether rationale should be shown for a permission.
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Request a permission.
     */
    fun request(
        activity: Activity,
        permission: String,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            requestCode
        )
    }

    /**
     * Request multiple permissions.
     */
    fun requestMultiple(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }
}
