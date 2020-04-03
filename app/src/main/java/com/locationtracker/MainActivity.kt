package com.locationtracker

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.appbase.activities.BaseActivity
import com.appbase.components.Locator
import com.appbase.components.PermissionListUtil
import com.appbase.components.SingleEventLiveData
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.appbase.showShortToast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*


class MainActivity : BaseActivity<MainViewModel>(), PermissionListUtil.PermissionListAskListener,
    Locator.Listener {

    private var ONLINE = "online"
    private var OFFLINE = "offline"
    private var locationMode: String = ONLINE
    private lateinit var locationManager: LocationManager
    private lateinit var locator: Locator
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val LOCATION_PERMISSION = 120
    private var permissionListUtil = PermissionListUtil(LOCATION_PERMISSION)

    private val currentAddressLD: SingleEventLiveData<String> by lazy { SingleEventLiveData<String>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }


    private fun trackLocation() {
        if (locationMode == ONLINE)
            locator.getLocation(Locator.Method.NETWORK, this)
        else
            locator.getLocation(Locator.Method.GPS, this)
    }

    override fun onNeedPermission(permission: String) {
        permissionListUtil.requestPermission(this, permission)
    }

    override fun onPermissionPreviouslyDenied(permission: String) {
        permissionListUtil.requestPermission(this, permission)
    }

    override fun onPermissionDisabled(permission: String) {
        goToAppSettings()
    }

    override fun onPermissionGranted(permission: String) {
        trackLocation()
    }

    override fun onAllPermissionGranted() {
        trackLocation()
    }

    override fun onAllPermissionRequested() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        showLogD("Permission callback called-------")
        if (requestCode == LOCATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLogD("PERMISSION IS GRANTED")
            trackLocation()
        } else
            showLogD("PERMISSION IS NOT GRANTED")

    }


    override fun onLocationFound(location: Location?) {
        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
        if (locationMode == ONLINE) {
            onlineLatLngTv.text = "Online Location : $latitude , $longitude"
            viewModel.getGeoEncodeData(location)
        } else
            offlineLatLngTv.text = "Offline Location : $latitude , $longitude"

        showLogE("Location founded : ")
//        getCurrentUserHomeTown(location, this, currentAddressLD)

        locationManager.removeUpdates(locator)
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        showShortToast("Location Getting Failed")
        locationManager.removeUpdates(locator)

    }




    override val layoutResId: Int = R.layout.activity_main

    override val rootLayout: View? by lazy { mainRootLayout }

    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {

    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }

    override fun onResume() {
        super.onResume()
        isLocationEnabled()
    }

    override fun initUI() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locator = Locator(this, locationManager)

        onlineLocationBtn.setOnClickListener {
            locationMode = ONLINE
            permissionListUtil.checkAndAskPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this
            )
        }

        offlineLocationBtn.setOnClickListener {
            locationMode = OFFLINE
            permissionListUtil.checkAndAskPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this
            )
        }

        viewModel.locationStatusLD.observe(this, androidx.lifecycle.Observer {
            if (it is ReturnResult.PositiveResult)
                locationTv.text = getErrorContentMsg(it)
            else
                showSnackBar(rootLayout!!, it)
        })



//        startService(Intent(applicationContext, BackgroundService::class.java))
    }

    override fun logOut() {

    }

    fun isLocationEnabled() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog = AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent);
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog?.dismiss() }
            val alert = alertDialog.create();
            alert.show();
        } else {
            val alertDialog = AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface") { dialog, which -> dialog?.dismiss() }

            val alert = alertDialog.create();
            alert.show();
        }
    }

}
