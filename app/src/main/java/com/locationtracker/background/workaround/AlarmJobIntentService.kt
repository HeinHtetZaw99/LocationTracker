package com.locationtracker.background.workaround

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.JobIntentService
import com.appbase.showLogD
import com.bumptech.glide.util.Util

class AlarmJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        /* your code here */
        /* reset the alarm */
        showLogD("setAlarmCtx", "started Bottom")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            AlarmReceiver.setAlarm(false)
        }
        stopSelf()
    }

    companion object {

        /* Give the Job a Unique Id */
        private val JOB_ID = 1000

        fun enqueueWork(ctx: Context, intent: Intent) {
            JobIntentService.enqueueWork(ctx, AlarmReceiver::class.java, JOB_ID, intent)
        }
    }
}