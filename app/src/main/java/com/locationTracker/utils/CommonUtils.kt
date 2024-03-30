package com.locationTracker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object CommonUtils {

    const val ACTION_START_SERVICE = "com.locationTracker.action.START_SERVICE"
    const val ACTION_STOP_SERVICE = "com.locationTracker.action.STOP_SERVICE"

    @SuppressLint("HardwareIds")
    fun generateDeviceUniqueID(context: Context): String {
        val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        return androidID
    }




}