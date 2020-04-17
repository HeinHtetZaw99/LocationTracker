package com.locationtracker.background

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import com.appbase.showLogD
import com.mapzen.android.lost.api.LocationResult
import com.mapzen.android.lost.api.LocationResult.EXTRA_LOCATION_RESULT

class LostLocationService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.extras
        val bundleKeySet: Set<String> = intent?.extras!!.keySet() // string key set

        for (key in bundleKeySet) { // traverse and print pairs
            showLogD("LOST" , "DATA from lost Service : ${data?.get(key)} from $key")
        }

        val location : LocationResult? = data?.get(EXTRA_LOCATION_RESULT) as LocationResult
        showLogD("LOST_LOCATION" , " location set from lost : ${location?.locations}")

        return START_STICKY
    }

}