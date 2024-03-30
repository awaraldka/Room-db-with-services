package com.locationTracker.backgroundService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.fithun.backgroundService.Actions

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                context.startService(it)
            }
        }
    }
}
