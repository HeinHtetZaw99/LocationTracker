package com.locationtracker.background

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import com.appbase.*
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.locationtracker.NotificationHelper
import com.locationtracker.R
import com.locationtracker.repository.LocationRepository
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import com.mapzen.android.lost.api.LocationRequest
import com.mapzen.android.lost.api.LocationResult
import com.mapzen.android.lost.api.LocationResult.EXTRA_LOCATION_RESULT
import com.mapzen.android.lost.api.LocationServices
import com.mapzen.android.lost.api.LostApiClient
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject


class LostLocationService : Service(), LostApiClient.ConnectionCallbacks {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var genericErrorMessageFactory: GenericErrorMessageFactory

    @Inject
    lateinit var mapper: LocationEntityMapper
    private val compositeDisposable = CompositeDisposable()
    private val TAG = "LostLocationService"
    private val handler = Handler()
    val lostApiClient: LostApiClient by lazy {
        LostApiClient.Builder(this).addConnectionCallbacks(this).build()
    }
    private val notificationHelper: NotificationHelper by lazy { NotificationHelper(this) }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
        lostApiClient.connect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationHelper.createNotificationChannel(
                getString(R.string.channel_description), getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }
        val notification =
            notificationHelper.createNotification("နေရာစစ်", "တည်နေရာအချက်အလက်များရယူနေပါသည်", null)
        startForeground(12345, notification)

        return START_REDELIVER_INTENT
    }

    fun getBundleSet(intent: Intent?) {
        val bundleKeySet: Set<String> = intent?.extras!!.keySet() // string key set
        for (key in bundleKeySet) { // traverse and print pairs
            showLogD("LOST", "DATA from lost Service : ${intent.extras?.get(key)} from $key")
        }
        val location: LocationResult? = intent.extras?.get(EXTRA_LOCATION_RESULT) as LocationResult
        showLogD("LOST_LOCATION", " location set from lost : ${location?.locations}")
    }


    private fun onLocationFound(location: Location?) {
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0
        showLogD(TAG, "Online Location From $TAG : $latitude , $longitude")
        processLocationData(latitude = latitude.toString(), longitude = longitude.toString())
        showLogE("Location founded : ")
    }

    override fun onConnected() {
        showLogD("onConnect from $TAG")
        val request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(180000)
            .setSmallestDisplacement(0f)
        LocationServices.FusedLocationApi.requestLocationUpdates(lostApiClient, request) {
            onLocationFound(it)
        }

        handler.postDelayed({
            val currentLocation = LocationServices.FusedLocationApi.getLastLocation(lostApiClient)
            if (currentLocation != null) {
                onLocationFound(currentLocation)
            }
        }, 180000)
    }

    override fun onConnectionSuspended() {

    }

    //todo refactor this method
    fun processLocationData(latitude: String, longitude: String) {
        locationRepository.getReverseGeoEncodeData(latitude, longitude).subscribe({
            val data = mapper.map(it)
            data.apply {
                retrievedBy = "SERVICE"
                this.latitude = latitude
                this.longitude = longitude
            }
            showLogD(TAG, "Reverse Geocoding from Service Succeeded : $data")
            //saving a data
            locationRepository.addLocationRepository(data).subscribe {
                showLogD("saved data to db from worker")
            }

            val log = "\n\n[${getTime()}-${getDate()}-($latitude,$longitude)-by SERVICE]"
            writeFileToDisk("Android/locationData/", "log.txt", log, false)
        }, {
            showLogE(
                TAG,
                "Reverse Geocoding from Service failed : ${genericErrorMessageFactory.getErrorMessage(
                    it
                )}"
            )
            locationRepository.saveLatLngOnly(latitude, longitude, getTime(), getDate()).subscribe {
                showLogD("saved partial data to db from worker")
            }
            //this scenerio may due to network failure and the user is in a new place , so saved it with timeStamp , time , date and the rest of the fields blank
        }).addTo(compositeDisposable = compositeDisposable)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(this, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }


}