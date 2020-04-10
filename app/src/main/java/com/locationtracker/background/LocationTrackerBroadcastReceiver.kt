package com.locationtracker.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.appbase.showLogD


class LocationTrackerBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val trackingWork  = OneTimeWorkRequest.Builder(LocationTrackerWorker::class.java)
            .build()
        WorkManager.getInstance(context!!).enqueue(trackingWork)
        /*Toast.makeText(context, "Triggering LocationWorker", Toast.LENGTH_LONG).show()*/
        showLogD("Starting locationTrackerWorker from broadcast Receiver")
    }

}