package co.hannalupi.locationtracker

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager

/**
 * A collection of convenience methods need for Android Runtime Permissions
 */
class PermissionUtils {

    companion object {

        val LOCATION_PERMISSION = ACCESS_FINE_LOCATION

        fun checkPermission(context : Context, permission : String) : Boolean {
            return context.checkSelfPermission(permission) != PackageManager.PERMISSION_DENIED
        }

    }
}