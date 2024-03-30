package com.locationTracker

import android.app.Application

import android.content.Context

class LocationTrackerApp : Application() {
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

    }




}
