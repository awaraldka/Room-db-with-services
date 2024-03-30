package com.locationTracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SavedPrefManager private constructor(context: Context) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Retrieve the boolean value from SharedPreferences for the given key.
     */
    fun getBooleanValue(key: String): Boolean {
        return preferences.getBoolean(key, false)
    }

    /**
     * Save a boolean value to SharedPreferences for the given key.
     */
    fun saveBooleanValue(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Retrieve the string value from SharedPreferences for the given key.
     */
     fun getStringValue(key: String): String? {
        return preferences.getString(key, "")
    }

    /**
     * Save a string value to SharedPreferences for the given key.
     */
     fun saveStringValue(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    /**
     * Retrieve the integer value from SharedPreferences for the given key.
     */
     fun getIntValue(key: String): Int {
        return preferences.getInt(key, 0)
    }

    /**
     * Save an integer value to SharedPreferences for the given key.
     */
     fun saveIntValue(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    /**
     * Retrieve the long value from SharedPreferences for the given key.
     */
    fun getLongValue(key: String?): Long {
        return preferences.getLong(key, 0L)
    }

    /**
     * Save a long value to SharedPreferences for the given key.
     */
    fun saveLongValue(key: String?, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    /**
     * Remove the preference for the given key from SharedPreferences.
     */
    fun removeFromPreference(key: String?) {
        preferences.edit().remove(key).apply()
    }

    companion object {
        // Constants for preference keys
        private const val DEVICE_ID = "DEVICE_ID"

        private var instance: SavedPrefManager? = null

        /**
         * Get an instance of SavedPrefManager.
         */
        fun getInstance(context: Context): SavedPrefManager {
            return instance ?: synchronized(this) {
                instance ?: SavedPrefManager(context).also { instance = it }
            }
        }

        // Add other preference utility methods here...

    }
}
