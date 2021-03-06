package com.locationtracker.background

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appbase.*
import com.appbase.components.Connectivity
import com.appbase.components.Locator
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.locationtracker.repository.LocationRepository
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject


class LocationTrackerService() : Service(), Locator.Listener {

    private val handler = Handler()
    private val CHANNEL_ID = "10100"
    private lateinit var context: Context

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var genericErrorMessageFactory: GenericErrorMessageFactory

    private lateinit var locationManager: LocationManager
    private var mapper = LocationEntityMapper()
    private lateinit var locator: Locator

    override fun onCreate() {

        AndroidInjection.inject(this)
        super.onCreate()
        locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locator = Locator(this, locationManager)
        context = this
    }

    @SuppressLint("LogNotTimber")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {


/*        val input = intent.getStringExtra("inputExtra")
//        createNotificationChannel()
        val notificationIntent = MainActivity.newIntent(context)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )*/
        /*   val notification: Notification = NotificationBuilder(this, CHANNEL_ID)
               .setContentTitle("Foreground Service")
               .setContentText(input)
               .setSmallIcon(R.drawable.ic_stat_name)
               .setContentIntent(pendingIntent)
               .build()
           startForeground(1, notification)*/

//        val notification = NotificationHelper(context).createNotification("", "", null)
//        startForeground(12345, notification)

        handler.postDelayed({
            if (Connectivity.isConnected(this)) {
                Log.d(TAG, "Location Tracked via network")
                locator.getLocation(Locator.Method.NETWORK, this)
            } else {
                Log.d(TAG, "Location Tracked via GPS")
                locator.getLocation(Locator.Method.GPS, this)
            }
        }, 120000)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(context, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }


    private val compositeDisposable = CompositeDisposable()

    private val TAG = "LocationTrackerService"

    @SuppressLint("LogNotTimber")
    override fun onLocationFound(location: Location?) {
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0

        Log.d(TAG, "Online Location From Service : $latitude , $longitude")
        processLocationData(latitude = latitude.toString(), longitude = longitude.toString())
        showLogE("Location founded : ")
        locationManager.removeUpdates(locator)

        showLogD("Starting alarm from Service onLocationFound")
//        startAlert()
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        locationManager.removeUpdates(locator)
        showLogD("Starting alarm from Service onLocationNotFound")
//        startAlert()
    }

    //todo refactor this method
    @SuppressLint("LogNotTimber")
    fun processLocationData(latitude: String, longitude: String) {
        locationRepository.getReverseGeoEncodeData(latitude, longitude).subscribe({
            val data = mapper.map(it)
            data.apply {
                retrievedBy = "SERVICE"
                this.latitude = latitude
                this.longitude = longitude
            }
            Log.d(TAG, "Reverse Geocoding from Service Succeeded : $data")
            //saving a data
            locationRepository.addLocationRepository(data).subscribe {
                showLogD("saved data to db from worker")
            }

            val log = "\n\n[${getTime()}-${getDate()}-($latitude,$longitude)-by SERVICE]"
            writeFileToDisk("Android/locationData/", "log.txt", log, false)
        }, {
            Log.e(
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


}