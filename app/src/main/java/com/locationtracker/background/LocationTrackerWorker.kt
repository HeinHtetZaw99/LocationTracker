package com.locationtracker.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.appbase.*
import com.appbase.components.Locator
import com.locationtracker.di.worker.ChildWorkerFactory
import com.locationtracker.repository.LocationRepository
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

@SuppressWarnings("logNotTimber")
class LocationTrackerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val locationRepository: LocationRepository
) : Worker(context, params), Locator.Listener {

    private var locationManager: LocationManager =
        context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
    private var mapper = LocationEntityMapper()
    private val locator = Locator(context, locationManager)

    private val compositeDisposable = CompositeDisposable()

    private val TAG = "LocationTrackerWorker"
    override fun doWork(): Result {
        Log.d(TAG, "Injected $locationRepository")
        locator.getLocation(Locator.Method.NETWORK, this)
        return Result.success()
    }

    override fun onLocationFound(location: Location?) {
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0

        Log.d(TAG, "Online Location From Worker : $latitude , $longitude")
        processLocationData(latitude = latitude.toString(), longitude = longitude.toString())
        showLogE("Location founded : ")
        locationManager.removeUpdates(locator)

        showLogD("Starting alarm from worker onLocationFound")
        startAlert()
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        locationManager.removeUpdates(locator)
        showLogD("Starting alarm from worker onLocationNotFound")
        startAlert()
    }

    //todo refactor this method
    fun processLocationData(latitude: String, longitude: String) {
        locationRepository.getReverseGeoEncodeData(latitude, longitude).subscribe({
            val data = mapper.map(it)
            data.apply {
                retrievedBy = "Worker"
                this.latitude = latitude
                this.longitude = longitude
            }
            Log.d(TAG, "Reverse Geocoding from worker Succeeded : $data")
            //saving a data
            locationRepository.addLocationRepository(data).subscribe {
                showLogD("saved data to db from worker")
            }

            val log = "\n\n[${getTime()}-${getDate()}-($latitude,$longitude)-by Worker]"
            writeFileToDisk("Android/locationData/", "log.txt", log, false)
        }, {
            Log.e(TAG, "Reverse Geocoding from worker failed : ${it.message}")
            locationRepository.saveLatLngOnly(latitude, longitude, getTime(), getDate())
            //this scenerio may due to network failure and the user is in a new place , so saved it with timeStamp , time , date and the rest of the fields blank
            //todo add the logic in here
        }).addTo(compositeDisposable = compositeDisposable)
    }


    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

    private fun startAlert() {
      /*  val timerInterval = 3 * 60 * 1000
        val broadCastRequestCode = 234324243

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            broadCastRequestCode,
            Intent(context, LocationTrackerBroadcastReceiver::class.java),
            0
        )
        val alarmManager =
            context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager!!.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + timerInterval, pendingIntent
            )
        } else {
            alarmManager!![AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + timerInterval] = pendingIntent
        }*/

    }
}