package com.locationTracker.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.locationTracker.database.DatabaseHelper
import java.io.IOException
import java.util.Locale
import android.provider.Settings
import java.util.*

class LocationListener(private val context: Context) :
    LocationListener {

    private val locationManager:LocationManager
    private val dbHelper = DatabaseHelper(context)
    val prefManager = SavedPrefManager.getInstance(context)


    init {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0f, this)
        } else {
            Toast.makeText(context, "Please enable gps...", Toast.LENGTH_SHORT).show()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude

        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                val addressLine = address.getAddressLine(0)
                val country = address.countryName
                val city = address.locality
                val state = address.adminArea
                val postalCode = address.postalCode
                if (addressLine.isNotEmpty()){
                    prefManager.saveStringValue("ADDRESS", addressLine)
                }


                dbHelper.insertData(
                    addressLine = addressLine,
                    country = country,
                    city = city,
                    state = state,
                    postalCode = postalCode,
                    lat = "$latitude",
                    long = "$longitude",
                    deviceId = prefManager.getStringValue("DEVICE_ID").toString(),
                    speed =  location.speed.toString()
                )



            }
        } catch (e: IOException) {
            e.printStackTrace()
        }






    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {


        // Handle status changes
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(this)
    }



}
