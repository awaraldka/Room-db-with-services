package com.fithun.permission
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionManager {

    private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 126


    private const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private const val postNotificationPermission = Manifest.permission.POST_NOTIFICATIONS




    private val backgroundPermissionRequestCode = 123


    private val allPermissions = arrayOf(
        locationPermission,
        postNotificationPermission
    )

    fun checkAndRequestPermissions(activity: Activity, retryCallback: ((String) -> Unit)? = null) {
        val permissionsToRequest = allPermissions.filter { !isPermissionGranted(activity, it) }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // All permissions are already granted
            // Proceed with your logic
        }
    }




    fun isPermissionGranted(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestBackgroundPermission(context:Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            backgroundPermissionRequestCode
        )
    }



    fun handlePermissionResult(
        activity: Activity,
        retryCallback: ((String) -> Unit)? = null,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                for (i in permissions.indices) {
                    if (permissions[i] == locationPermission &&
                        (grantResults[i] == PackageManager.PERMISSION_DENIED ||
                                grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    ) {
                        // Location permission denied or granted
                        retryCallback?.invoke(locationPermission)
                        return
                    }
                }
            }


            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                for (i in permissions.indices) {
                    if (permissions[i] == postNotificationPermission && grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        retryCallback?.invoke(postNotificationPermission)
                        return
                    }
                }
            }


        }
    }
}
