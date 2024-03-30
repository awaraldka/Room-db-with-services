package com.locationTracker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fithun.permission.PermissionManager
import com.locationTracker.R
import com.locationTracker.backgroundService.EndlessService
import com.locationTracker.clickInterface.LocationDenyInterface
import com.locationTracker.database.DatabaseHelper
import com.locationTracker.databinding.ActivityStartLocationBinding
import com.locationTracker.modelClass.LocationData
import com.locationTracker.utils.CommonUtils
import com.locationTracker.utils.CommonUtils.ACTION_START_SERVICE
import com.locationTracker.utils.CommonUtils.ACTION_STOP_SERVICE
import com.locationTracker.utils.ExcelExporter
import com.locationTracker.utils.PopUpDialogs
import com.locationTracker.utils.PopUpDialogs.locationAllowPermission
import com.locationTracker.utils.SavedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StartLocationActivity : AppCompatActivity() , LocationDenyInterface {

    private lateinit var binding:ActivityStartLocationBinding
    private val dbHelper = DatabaseHelper(this)

    private var isServiceRunning = false



    @SuppressLint("Range", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prefManager = SavedPrefManager.getInstance(this)

        PermissionManager.checkAndRequestPermissions(this@StartLocationActivity)


        isServiceRunning = prefManager.getBooleanValue("isServiceStart")

        if (isServiceRunning){
            startUpdateCoroutine(prefManager)
        }


        binding.startService.setOnClickListener {
            isServiceRunning = prefManager.getBooleanValue("isServiceStart")
            if(PermissionManager.isPermissionGranted(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                if (!isServiceRunning){
                    Toast.makeText(this, "Location updates started.", Toast.LENGTH_SHORT).show()
                    prefManager.saveBooleanValue("isServiceStart",true)
                    prefManager.saveStringValue("DEVICE_ID",CommonUtils.generateDeviceUniqueID(this))
                    startUpdateCoroutine(prefManager)
                    manageService(ACTION_START_SERVICE)
                }else{
                    PopUpDialogs.openPopUp(this, "Service already running.")
                }


            }else{
                PermissionManager.checkAndRequestPermissions(this@StartLocationActivity)
            }
        }

        binding.stopService.setOnClickListener {
            isServiceRunning = prefManager.getBooleanValue("isServiceStart")
            if (isServiceRunning){
                prefManager.saveBooleanValue("isServiceStart",false)
                Toast.makeText(this, "Location updates stopped.", Toast.LENGTH_SHORT).show()
                binding.locationName.text = getString(R.string.location_data)
                stopUpdates()
                manageService(ACTION_STOP_SERVICE)
            }else{
                PopUpDialogs.openPopUp(this, "Please start service to get updates.")
            }




        }


        binding.downloadData.setOnClickListener {
            val jsonArray = dbHelper.fetchDataByDeviceId(prefManager.getStringValue("DEVICE_ID").toString())
            val locationDataList  : ArrayList<LocationData> =  arrayListOf()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val addressLine = jsonObject.getString("addressLine")
                val country = jsonObject.getString("country")
                val city = jsonObject.getString("city")
                val state = jsonObject.getString("state")
                val postalCode = jsonObject.getString("postalCode")
                val lat = jsonObject.getString("lat")
                val long = jsonObject.getString("long")
                val deviceId = jsonObject.getString("deviceId")
                val speed = jsonObject.getString("speed")

                val locationData = LocationData(addressLine, country, city, state, postalCode, lat, long,deviceId,speed)
                locationDataList.add(locationData)
            }

            if (locationDataList.isEmpty()){
                PopUpDialogs.openPopUp(this, "No data found.")
                return@setOnClickListener
            }
            val excelExporter = ExcelExporter(this)
            excelExporter.generateExcel(locationDataList)
        }





    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        PermissionManager.handlePermissionResult(
            this,
            retryCallback = { deniedPermission ->

                when (deniedPermission) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> {
                        locationAllowPermission(
                            "Please allow location permission to track all the time.",
                            this@StartLocationActivity,
                            "Location",
                            this@StartLocationActivity
                        )
                    }

                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                        locationAllowPermission(
                            "Please allow location permission to track all the time.",
                            this@StartLocationActivity,
                            "Location",
                            this@StartLocationActivity
                        )
                    }

                    Manifest.permission.POST_NOTIFICATIONS -> {
                        locationAllowPermission(
                            "Please allow permission for notifications.",
                            this@StartLocationActivity,
                            "",
                            this@StartLocationActivity
                        )
                    }






                }

            },
            requestCode = requestCode,
            permissions = permissions,
            grantResults = grantResults
        )


    }

    override fun openSettings(isForLocation: String) {
        if (isForLocation == "Location") {
            PermissionManager.requestBackgroundPermission(this@StartLocationActivity)
        } else {
            val intent = Intent()
            intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            intent.data = Uri.fromParts("package", this.packageName, null)
            startActivity(intent)
        }
    }

    private fun manageService(value:String) {
        val startServiceIntent = Intent(this, EndlessService::class.java)
        startServiceIntent.action = value
        startForegroundService(startServiceIntent)
    }

    private var job: Job? = null



    private fun startUpdateCoroutine(prefManager:SavedPrefManager) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                updateUI(prefManager)
                delay(5000)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(prefManager:SavedPrefManager) {
        binding.locationName.text = "${prefManager.getStringValue("ADDRESS")}"
    }

    private fun stopUpdates() {
        job?.cancel()
    }



}