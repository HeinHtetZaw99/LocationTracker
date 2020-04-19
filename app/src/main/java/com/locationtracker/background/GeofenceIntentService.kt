package com.locationtracker.background

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.appbase.showLogD
import com.locationtracker.NotificationHelper

class GeofenceIntentService  : Service(){
    private val notificationHelper : NotificationHelper by lazy { NotificationHelper(applicationContext) }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showLogD("GeofenceIntentService got called ")
        val notification = notificationHelper.createNotification("Please be careful out there" , "Please don't forget to wear mask and wash your hands",null)
        notificationHelper.showNotification(notification!!)
        return START_STICKY
    }

}
