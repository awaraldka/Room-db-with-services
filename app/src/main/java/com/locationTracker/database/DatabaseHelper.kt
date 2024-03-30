package com.locationTracker.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LocationData"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create your tables here
        db?.execSQL("CREATE TABLE IF NOT EXISTS LOCATION (id INTEGER PRIMARY KEY, addressLine TEXT, country INTEGER, city TEXT , state TEXT, postalCode TEXT, lat TEXT, long TEXT, deviceId TEXT,speed TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Upgrade the database if needed
    }




    fun insertData(
        addressLine: String,
        country: String,
        city: String,
        state: String,
        postalCode: String,
        lat: String,
        long: String,
        deviceId: String,
        speed: String,
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("addressLine", addressLine)
            put("country", country)
            put("city", city)
            put("state", state)
            put("postalCode", postalCode)
            put("lat", lat)
            put("long", long)
            put("deviceId", deviceId)
            put("speed", speed)
        }
        db.insert("LOCATION", null, values)
        Log.d("insertData", "insertData: Data Saved")
        db.close()
    }




    @SuppressLint("Range")
    fun fetchDataByDeviceId(deviceId: String): JSONArray {
        val db = readableDatabase
        val selection = "deviceId = ?"
        val selectionArgs = arrayOf(deviceId)
        val cursor = db.query("LOCATION", null, selection, selectionArgs, null, null, null)

        val jsonArray = JSONArray()

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val jsonObject = JSONObject()
                jsonObject.put("addressLine", cursor.getString(cursor.getColumnIndex("addressLine")))
                jsonObject.put("country", cursor.getString(cursor.getColumnIndex("country")))
                jsonObject.put("city", cursor.getString(cursor.getColumnIndex("city")))
                jsonObject.put("state", cursor.getString(cursor.getColumnIndex("state")))
                jsonObject.put("postalCode", cursor.getString(cursor.getColumnIndex("postalCode")))
                jsonObject.put("lat", cursor.getString(cursor.getColumnIndex("lat")))
                jsonObject.put("long", cursor.getString(cursor.getColumnIndex("long")))
                jsonObject.put("deviceId", cursor.getString(cursor.getColumnIndex("deviceId")))
                jsonObject.put("speed", cursor.getString(cursor.getColumnIndex("speed")))

                jsonArray.put(jsonObject)
            }
        }

        return jsonArray
    }


}
