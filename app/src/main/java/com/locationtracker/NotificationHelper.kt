package com.locationtracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

import com.locationtracker.activities.MainActivity


class NotificationHelper(private val mContext: Context) {
    private val mNotificationManager: NotificationManager? by lazy {
        mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var mBuilder: NotificationCompat.Builder? = null

    /**
     * Create and push the notification
     */
    fun createNotification(
        title: String,
        message: String,
        bitmap: Bitmap?
    ): Notification? {
        val notiTitle = title

        val notiMessage = message
        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = MainActivity.newIntent(context = mContext)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder = NotificationCompat.Builder(
            mContext,
            mContext.getString(R.string.notification_channel_id)
        )
        mBuilder!!.setSmallIcon(R.drawable.ic_app_logo)
        mBuilder!!.setContentTitle(notiTitle)
            .setContentText(notiMessage)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)
        if (bitmap != null) {
            val bpStyle =
                NotificationCompat.BigPictureStyle()
            bpStyle.bigPicture(bitmap).build()
            mBuilder!!.setStyle(bpStyle)
        }

        return mBuilder?.build()
    }

    fun showNotification(notification : Notification){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                mContext.getString(R.string.channel_name),
                importance
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(
                100,
                200,
                300,
                400,
                500,
                400,
                300,
                200,
                400
            )
            assert(mNotificationManager != null)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager!!.notify(0 /* Request Code */, notification)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "10001"
    }


}