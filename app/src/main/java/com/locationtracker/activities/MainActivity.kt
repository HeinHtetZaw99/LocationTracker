package com.locationtracker.activities

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.appbase.*
import com.appbase.activities.BaseActivity
import com.appbase.fragments.BaseFragment
import com.locationtracker.LocationTrackerApplication
import com.locationtracker.R
import com.locationtracker.background.GeofenceIntentService
import com.locationtracker.background.LocationTrackerBroadcastReceiver
import com.locationtracker.background.LocationTrackerService
import com.locationtracker.background.LostLocationService
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.fragments.LocationHistoryFragment
import com.locationtracker.viewmodels.MainViewModel
import com.mapzen.android.lost.api.*
import com.mapzen.android.lost.api.Geofence.NEVER_EXPIRE
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity<MainViewModel>(),
    LocationHistoryFragment.OnListFragmentInteractionListener, LostApiClient.ConnectionCallbacks {


    private val homeFragment = HomeFragment()

    val lostApiClient: LostApiClient by lazy {
        LostApiClient.Builder(this).addConnectionCallbacks(this).build()
    }
    var currentLocation: Location? = null

    private val historyFragment = HistoryFragment()
    private var activeFragment: BaseFragment = homeFragment
    private var lastVisitedFragment: Int = R.id.nav_home
    private var fragmentList = SparseArray<BaseFragment>().apply {
        put(R.id.nav_home, homeFragment)
        put(R.id.nav_history, historyFragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    override val layoutResId: Int = R.layout.activity_main

    override val rootLayout: ViewGroup? by lazy { mainRootLayout }

    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {

    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }


    override fun initUI() {
        val logHeader =
            "\n---------------------------------------------\nWorkManger Deployed @ ${getTime()} ${getDate()}\n---------------------------------------------\n"
        writeFileToDisk("Android/locationData/", "log.txt", logHeader, false)
//        initPeriodicWork()


        fragmentList.buildFragmentList(
            supportFragmentManager,
            R.id.fragmentContainer,
            R.id.nav_home
        )
        bottomNavigationBar.handleNavigationTransactions(fragmentList, ::makeFragmentTransaction)


        val receiver = ComponentName(this, LocationTrackerBroadcastReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )


        lostApiClient.connect()

        createNotificationChannel()

    }

    private fun makeFragmentTransaction(
        currentFragment: BaseFragment,
        @IdRes selectedNavID: Int
    ) {
        lastVisitedFragment = selectedNavID
        supportFragmentManager.beginTransaction().hide(activeFragment)
            .show(currentFragment).commit()
        activeFragment = currentFragment
    }

    private fun initPeriodicWork() {
        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WorkManager.getInstance(this).enqueueUniqueWork(
                    "Main",
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequestBuilder<LocationTrackerWorker>().build()
                )
            } else {
                //todo haven't done yet
            }
    */
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            LocationTrackerApplication.BroadcastReceiverCode,
            Intent(this, LocationTrackerBroadcastReceiver::class.java),
            0
        )
        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager!!.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + 100, 180000, pendingIntent
            )
        } else {
            /*       alarmManager!![AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                           + 1000] = pendingIntent*/
            alarmManager!!.setRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + 100, 180000, pendingIntent
            )
        }

    }

    fun startLocationTrackingService() {
        val serviceIntent = Intent(this, LocationTrackerService::class.java)
        startService(serviceIntent)
    }

    override fun logOut() {

    }

    fun changeNavigationBarVisibility(visibility: Int) {
//        bottomNavigationBar.visibility = visibility
        if (visibility == View.GONE) {
            slideDown(bottomNavigationBar, Gravity.BOTTOM, rootLayout!!)
        } else {
            slideUp(bottomNavigationBar, Gravity.BOTTOM, rootLayout!!)
        }
    }

    fun getHomeViewModel() = viewModel

    override fun onListFragmentInteraction(adapterPosition: Int) {
        showLogE("Adapter Position $adapterPosition")
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onConnected() {
        showLogD("Lost Service is connected")
        notifyUserToGetProtection("12345", 16.835154, 96.128817, 15f)
        currentLocation =
            LocationServices.FusedLocationApi.getLastLocation(
                lostApiClient
            )

        historyFragment.showCurrentLocationOnMap(currentLocation)
//        getLocationUpdatesInBackground(1234)
    }

    override fun onConnectionSuspended() {
        showLogD("Lost service is in connectionSuspended state")
    }

    fun notifyUserToGetProtection(
        requestId: String,
        homeLatitude: Double,
        homeLongitude: Double,
        radius: Float
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(homeLatitude, homeLongitude, radius)
            .setExpirationDuration(NEVER_EXPIRE)
            .build()
        val request = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .build()
        val serviceIntent =
            Intent(applicationContext, GeofenceIntentService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, serviceIntent, 0)
        LocationServices.GeofencingApi.addGeofences(lostApiClient, request, pendingIntent);
    }

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getString(R.string.notification_channel_id), name, importance)
            channel.description = description
            channel.setShowBadge(true)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            Objects.requireNonNull(notificationManager)
                .createNotificationChannel(channel)
        }
    }

    fun getLocationUpdatesInBackground(requestCode: Int) {
        val request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(180000)

        val intent =  Intent(this , LostLocationService::class.java);
        val pendingIntent = PendingIntent.getService(
            this, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
        LocationServices.FusedLocationApi.requestLocationUpdates(
            lostApiClient,
            request,
            pendingIntent
        );
    }

}
