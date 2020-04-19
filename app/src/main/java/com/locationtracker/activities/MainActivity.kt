package com.locationtracker.activities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.appbase.*
import com.appbase.activities.BaseActivity
import com.appbase.fragments.BaseFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.locationtracker.R
import com.locationtracker.background.GeofenceIntentService
import com.locationtracker.background.LocationTrackerBroadcastReceiver
import com.locationtracker.background.LostLocationService
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.fragments.LocationHistoryFragment
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.viewmodels.MainViewModel
import com.mapzen.android.lost.api.*
import com.mapzen.android.lost.api.Geofence.NEVER_EXPIRE
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.osmdroid.config.Configuration
import java.io.File
import java.io.IOException


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
        val osmConf = Configuration.getInstance()
        val basePath = File(cacheDir.absolutePath, "osmdroid")
        osmConf.osmdroidBasePath = basePath
        val tileCache = File(osmConf.osmdroidBasePath.absolutePath, "tile")
        osmConf.osmdroidTileCache = tileCache

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

        createNotificationChannel(
            getString(R.string.channel_description),
            getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        if (isLocationPermissionGranted())
            lostApiClient.connect()

        readContactListFromJson()

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


    fun startLocationTrackingService() {
        showLogD("Location Tracking Activated")
        val serviceIntent = Intent(this, LostLocationService::class.java)
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
        try {
            showLogD("Lost Service is connected")

            currentLocation =
                LocationServices.FusedLocationApi.getLastLocation(
                    lostApiClient
                )

            historyFragment.showCurrentLocationOnMap(currentLocation)
//        getLocationUpdatesInBackground(1234)
        }catch (e : SecurityException){
            showLogE("Error in onConnect :",e)
        }
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


    fun getLocationUpdatesInBackground(requestCode: Int) {
        val request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(180000)

        val intent = Intent(this, LostLocationService::class.java);
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

    fun readContactListFromJson() {

        try {
            val inStream = resources.assets.open("contact_list.json")
            val size = inStream.available()
            val buffer = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            val listType = object : TypeToken<List<ContactVO>>() {}.type
            val contactListString = String(buffer)
            val contactList = Gson().fromJson<List<ContactVO>>(contactListString, listType)
            showLogD("contactList ${contactList.size}")
            viewModel.saveContactListToDB(contactList)

        } catch (e: IOException) {
            showLogE("error in parsing json ", e)
        } catch (e: JSONException) {
            showLogE("error in parsing json ", e)
        }

    }


    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

}
