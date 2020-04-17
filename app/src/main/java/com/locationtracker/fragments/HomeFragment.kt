package com.locationtracker.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.appbase.*
import com.appbase.components.Locator
import com.appbase.components.PermissionListUtil
import com.appbase.fragments.BaseFragment
import com.appbase.models.vos.ReturnResult
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.sources.cache.data.LocationEntity.Companion.toCSV
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), PermissionListUtil.PermissionListAskListener,
    Locator.Listener {

    private val handler = android.os.Handler()
    private val locationManager: LocationManager by lazy {
        parentActivity.getSystemService(
            AppCompatActivity.LOCATION_SERVICE
        ) as LocationManager
    }
    private val locator: Locator by lazy { Locator(view!!.context, locationManager) }
    private var exportAsCSV = false
    private val LOCATION_PERMISSION = 120
    private val ALL_APP_PERMISSION = 130

    private var permissionListUtil = PermissionListUtil(ALL_APP_PERMISSION)


    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override var fragmentLayout: Int = R.layout.fragment_home

    val parentActivity: MainActivity by lazy { activity as MainActivity }

    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

        permissionListUtil.checkAndAskPermissions(
            parentActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            this
        )

        locationEnabledView.setVisible(true)
        locationDisabledView.setVisible(false)
        writeStoragePermissionEnabledView.setVisible(true)
        writeStoragePermissionDisabledView.setVisible(false)

        allowAccessLocationBtn.setOnClickListener {
            parentActivity.showShortToast("Please enable the location permission in the settings")
            parentActivity.goToAppSettings()
        }


        viewModel.locationStatusLD.observe(this, androidx.lifecycle.Observer {
            if (it is ReturnResult.PositiveResult) {
                currentLocationTv.text = parentActivity.getErrorContentMsg(it)
                if (isWriteStoragePermissionGranted()) {
                    writeFileToDisk(
                        "Android/locationData/",
                        "location.txt",
                        parentActivity.getErrorContentMsg(it)
                        , false
                    )
                } else {
                    showLogD("Permission is not granted. so no log is written")
                }
            } else
                parentActivity.showSnackBar(view, it)
        })

        viewModel.locationListLD.observe(this, Observer {
            if (exportAsCSV) {
                writeFileToDisk(
                    "Android/locationData/",
                    "location_data_csv.txt",
                    toCSV(it),
                    true
                )
                parentActivity.showShortToast("Exporting as CSV")
                exportAsCSV = false
            }
        })

        checkSymptomsBtn.setOnClickListener {
            startActivity(SelfExaminationActivity.newIntent(parentActivity))
        }
        convertToCSVBtn.setOnClickListener {
            exportAsCSV = true
            permissionListUtil.checkAndAskPermissions(
                parentActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                this
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        showLogD("Permission callback called-------")
        if (requestCode == ALL_APP_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLogD("PERMISSION IS GRANTED")
            trackLocation()
        } else if (requestCode == ALL_APP_PERMISSION && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            showLogD("PERMISSION IS GRANTED")
            if (exportAsCSV)
                convertToCSV()
        } else
            showLogD("PERMISSION IS NOT GRANTED")
    }


    override fun onLocationFound(location: Location?) {
        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
        currentLatLngTv.text = "Location : $latitude , $longitude"
        viewModel.getGeoEncodeData(location!!)
        showLogE("Location founded : ")

        locationManager.removeUpdates(locator)
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        showShortToast("Location Getting Failed")
        locationManager.removeUpdates(locator)

    }


    private fun trackLocation() {
        //TODO uncomment me again
//        parentActivity.startLocationTrackingService()
    }


    override fun onNeedPermission(permission: String) {
        permissionListUtil.requestPermission(parentActivity, permission)
    }

    override fun onPermissionPreviouslyDenied(permission: String) {

        permissionListUtil.requestPermission(parentActivity, permission)
    }

    override fun onPermissionDisabled(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                //showing layout which is saying user to enabling location
                locationEnabledView.setVisible(false)
                locationDisabledView.setVisible(true)
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                //showing layout which is saying user to enabling location
                writeStoragePermissionEnabledView.setVisible(false)
                writeStoragePermissionDisabledView.setVisible(true)
            }
        }

    }

    override fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                if (isLocationEnabled())
                    trackLocation()
                else
                    showForceGPSEnableDialog()
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                //showing layout which is saying user to enabling location
                convertToCSV()
            }
        }

    }

    private fun convertToCSV() {
        viewModel.getLocationHistory()
    }

    override fun onAllPermissionGranted() {
        if (isLocationEnabled())
            trackLocation()
        else
            showForceGPSEnableDialog()

        convertToCSV()
    }

    override fun onAllPermissionRequested() {

    }

    private fun isLocationEnabled(): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isWriteStoragePermissionGranted() =
        ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED


    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

    private fun showForceGPSEnableDialog() {
        val alertDialog = AlertDialog.Builder(parentActivity)
        alertDialog.setTitle("Enable Location")
        alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.")
        alertDialog.setPositiveButton("Location Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog?.dismiss() }
        val alert = alertDialog.create()
        alert.show()
    }


}
