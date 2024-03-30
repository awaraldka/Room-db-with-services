package com.locationTracker.backgroundService

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.locationTracker.utils.LocationListener
import com.locationTracker.R
import com.locationTracker.ui.StartLocationActivity
import com.locationTracker.utils.CommonUtils.ACTION_START_SERVICE
import com.locationTracker.utils.CommonUtils.ACTION_STOP_SERVICE

class EndlessService : Service() {

    private lateinit var stepCountListener: LocationListener
    private lateinit var notificationManager: NotificationManager
    val notificationChannelId = "Location tracking"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> {
                val notification = createNotification()
                startForeground(1, notification)
                stepCountListener = LocationListener(applicationContext)
                setServiceState(this, ServiceState.STARTED)
            }
            ACTION_STOP_SERVICE -> {
                    stopSelf()
                    stepCountListener.stopLocationUpdates()
                    notificationManager.cancel(1)


            }

            }



        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {

        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        }
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(
            this,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }




    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                notificationChannelId,
                "Location  tracking",
                NotificationManager.IMPORTANCE_LOW
            ).let {
                it.description = "Location  tracking"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true) // Disable vibration
                it.vibrationPattern = null // Set vibration pattern to null
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, StartLocationActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(this, notificationChannelId) else Notification.Builder(this)

        return builder
            .setContentTitle("Location  Tracker")
            .setContentText("Your location is being track")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Ticker text")
            .build()
    }








}
