package com.locationTracker.utils

import android.app.AlertDialog
import android.content.Context
import com.locationTracker.R
import com.locationTracker.clickInterface.LocationDenyInterface

object PopUpDialogs {
    fun locationAllowPermission(message: String, context: Context, isForLocation:String, location: LocationDenyInterface) {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setTitle("Location Tracker")
        builder.setMessage(message)

        builder.setPositiveButton("Go") { _, _ ->
            alertDialog!!.dismiss()
            location.openSettings(isForLocation)
        }

        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog.show()
    }
    fun openPopUp(context: Context,message: String) {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setTitle("Location Tracker")
        builder.setMessage(message)

        builder.setPositiveButton("Ok") { _, _ ->
            alertDialog!!.dismiss()

        }

        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog.show()
    }


}