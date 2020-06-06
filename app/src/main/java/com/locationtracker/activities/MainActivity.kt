package com.locationtracker.activities

import android.Manifest
import android.app.NotificationManager
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
import com.appbase.components.PermissionListUtil
import com.appbase.fragments.BaseFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.locationtracker.R
import com.locationtracker.background.LocationTrackerBroadcastReceiver
import com.locationtracker.background.LostLocationService
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.viewmodels.MainViewModel
import com.mapzen.android.lost.api.LocationServices
import com.mapzen.android.lost.api.LostApiClient
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.osmdroid.config.Configuration
import java.io.File
import java.io.IOException


class MainActivity : BaseActivity<MainViewModel>(), LostApiClient.ConnectionCallbacks,
    PermissionListUtil.PermissionListAskListener {
    private val ALL_APP_PERMISSION = 130
    private val LOCATION_PERMISSION = 120
    private val STORAGE_PERMISSION = 110
    private var permissionListUtil = PermissionListUtil(ALL_APP_PERMISSION)
    private var exportAsCSV = false

    var isManuallyInvokedPermissionAsking = false

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

        viewModel.locationListLD.observe(this, androidx.lifecycle.Observer {
            if (exportAsCSV) {
                writeFileToDisk(
                    "Android/locationData/",
                    "location_data.csv",
                    LocationEntity.toCSV(it),
                    true
                )
                showShortToast("Exporting as CSV")
                exportAsCSV = false
                logEvent(
                    FunctionType.CSVConversion,
                    Bundle().apply { putString("Date", getDate()) })
            }
        })

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
        readClinicsListFromJson()

    }

    fun askPermissions() {
        permissionListUtil.with(ALL_APP_PERMISSION)
        permissionListUtil.checkAndAskPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            this
        )
    }

    private fun makeFragmentTransaction(
        currentFragment: BaseFragment,
        @IdRes selectedNavID: Int
    ) {
        lastVisitedFragment = selectedNavID
        supportFragmentManager.beginTransaction().hide(activeFragment)
            .show(currentFragment).commit()
        activeFragment = currentFragment

        if (activeFragment is HistoryFragment) {
            (activeFragment as HistoryFragment).showCurrentLocationOnMap(currentLocation)
        }
    }


    fun startLocationTrackingService() {
        lostApiClient.connect()
        showLogD("Location Tracking Activated")
        val serviceIntent = Intent(this, LostLocationService::class.java)
        startService(serviceIntent)
        logEvent(FunctionType.LocationTracking, Bundle().apply { putString("Date", getDate()) })
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
        } catch (e: SecurityException) {
            showLogE("Error in onConnect :", e)
        }
    }

    override fun onConnectionSuspended() {
        showLogD("Lost service is in connectionSuspended state")
    }

    /* fun notifyUserToGetProtection(
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
 */
    private fun readContactListFromJson() {
        val contactListString = readFromJson("contact_list.json")
        val contactList = Gson().fromJson<List<ContactVO>>(
            contactListString,
            object : TypeToken<List<ContactVO>>() {}.type
        )
        showLogD("contactList ${contactList.size}")
        viewModel.saveContactListToDB(contactList)
    }


    private fun readClinicsListFromJson() {
        val contactListString = readFromJson("foc_clinic_list.json")
        val contactList = Gson().fromJson<List<FocClinicVO>>(
            contactListString,
            object : TypeToken<List<FocClinicVO>>() {}.type
        )
        showLogD("foc clinics list ${contactList.size}")
        viewModel.saveClinicsListToDB(contactList)
    }


    private fun readFromJson(fileName: String): String {
        return try {
            val inStream = resources.assets.open(fileName)
            val size = inStream.available()
            val buffer = ByteArray(size)
            inStream.read(buffer)
            inStream.close()
            String(buffer)
        } catch (e: IOException) {
            showLogE("error in parsing json ", e)
            ""
        } catch (e: JSONException) {
            showLogE("error in parsing json ", e)
            ""
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        showLogD("Permission callback called from main-------")
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == ALL_APP_PERMISSION || requestCode == LOCATION_PERMISSION) {
                showLogD("PERMISSION IS GRANTED")
                onPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (requestCode == STORAGE_PERMISSION) {
                showLogD("PERMISSION IS GRANTED")
                convertToCSV()
            }
        } else if (grantResults.isNotEmpty() && grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            showLogD("PERMISSION IS GRANTED")
            convertToCSV()
        } else {
            showLogD("PERMISSION IS NOT GRANTED")
            if (requestCode == LOCATION_PERMISSION) {
                showLongToast("ကျေးဇူးပြု၍ location permission ကိုဖွင့်ပေးပါ")
            } else if (requestCode == STORAGE_PERMISSION) {
                showLongToast("ကျေးဇူးပြု၍ location permission ကိုဖွင့်ပေးပါ")
            }
//            goToAppSettings()
        }

    }

    fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onNeedPermission(permission: String) {
        permissionListUtil.requestPermission(this, permission)

    }

    override fun onPermissionPreviouslyDenied(permission: String) {
        permissionListUtil.requestPermission(this, permission)
    }

    override fun onPermissionDisabled(permission: String) {
        if (isManuallyInvokedPermissionAsking) {
            when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    //showing layout which is saying user to enabling location
                    historyFragment.onResume()
                    showLongToast("ကျေးဇူးပြု၍ location permission ကိုဖွင့်ပေးပါ")
                    goToAppSettings()
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    //showing layout which is saying user to enabling location
                    showLongToast("ကျေးဇူးပြု၍ storage permission ကိုဖွင့်ပေးပါ")
                    goToAppSettings()
                }
            }
            isManuallyInvokedPermissionAsking = false
        }

    }

    override fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                startLocationTrackingService()
                homeFragment.onResume()
                historyFragment.onResume()
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                //showing layout which is saying user to enabling location
                viewModel.getLocationHistory()
            }
        }
        isManuallyInvokedPermissionAsking = false
    }


    override fun onAllPermissionGranted() {
        isManuallyInvokedPermissionAsking = false
        startLocationTrackingService()
        viewModel.getLocationHistory()
    }

    override fun onAllPermissionRequested() {

    }

    private fun checkWriteStoragePermission() {
        permissionListUtil.with(STORAGE_PERMISSION)
        permissionListUtil.checkAndAskPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            this
        )
    }

    fun convertToCSV() {
        isManuallyInvokedPermissionAsking = true
        exportAsCSV = true
        checkWriteStoragePermission()
    }

    fun checkLocationPermission() {
        permissionListUtil.with(LOCATION_PERMISSION)
        permissionListUtil.checkAndAskPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            this
        )
    }

}
