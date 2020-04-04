package com.locationtracker.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appbase.components.Locator
import com.appbase.components.PermissionListUtil
import com.appbase.fragments.BaseFragment
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.appbase.showShortToast
import com.appbase.writeFileToDisk
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(), PermissionListUtil.PermissionListAskListener,
    Locator.Listener {

    private val locationManager: LocationManager by lazy {
        parentActivity.getSystemService(
            AppCompatActivity.LOCATION_SERVICE
        ) as LocationManager
    }
    private val locator: Locator by lazy { Locator(view!!.context, locationManager) }

    private val LOCATION_PERMISSION = 120
    private val ALL_APP_PERMISSION = 130

    private var permissionListUtil = PermissionListUtil(ALL_APP_PERMISSION)

    private var ONLINE = "online"
    private var OFFLINE = "offline"
    private var locationMode: String = ONLINE

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override var fragmentLayout: Int = R.layout.fragment_home

    val parentActivity: MainActivity by lazy { activity as MainActivity }

    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }

    override fun onNetworkError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

        onlineLocationBtn.setOnClickListener {
            locationMode = ONLINE
            permissionListUtil.checkAndAskPermissions(
                parentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this
            )
        }

        offlineLocationBtn.setOnClickListener {
            locationMode = OFFLINE
            permissionListUtil.checkAndAskPermissions(
                parentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                this
            )
        }

        viewModel.locationStatusLD.observe(this, androidx.lifecycle.Observer {
            if (it is ReturnResult.PositiveResult) {
                locationTv.text = parentActivity.getErrorContentMsg(it)
                writeFileToDisk(
                    "Android/locationData/",
                    "location.txt",
                    parentActivity.getErrorContentMsg(it)
                )
            } else
                parentActivity.showSnackBar(view, it)
        })

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

        locationManager.removeUpdates(locator)
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        showShortToast("Location Getting Failed")
        locationManager.removeUpdates(locator)

    }


    private fun trackLocation() {
        if (locationMode == ONLINE)
            locator.getLocation(Locator.Method.NETWORK, this)
        else
            locator.getLocation(Locator.Method.GPS, this)
    }


    override fun onNeedPermission(permission: String) {
        permissionListUtil.requestPermission(parentActivity, permission)
    }

    override fun onPermissionPreviouslyDenied(permission: String) {
        permissionListUtil.requestPermission(parentActivity, permission)
    }

    override fun onPermissionDisabled(permission: String) {
        parentActivity.goToAppSettings()
    }

    override fun onPermissionGranted(permission: String) {
        trackLocation()
    }

    override fun onAllPermissionGranted() {
        trackLocation()
    }

    override fun onAllPermissionRequested() {

    }


}
