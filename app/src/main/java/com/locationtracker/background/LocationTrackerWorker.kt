package com.locationtracker.background

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
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
    /*   private val reverseGeocodeService: ReverseGeocodeService*/
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
        //todo remove this when released
        Toast.makeText(context,"Location Tracker Running From Background",Toast.LENGTH_SHORT).show()
        showLogE("Location founded : ")
        locationManager.removeUpdates(locator)
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        locationManager.removeUpdates(locator)

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
            writeFileToDisk("Android/locationData/", "log.txt", log)
        }, {
            Log.e(TAG, "Reverse Geocoding from worker failed : ${it.message}")
            //this scenerio may due to network failure and the user is in a new place , so saved it with timeStamp , time , date and the rest of the fields blank
            //todo add the logic in here
        }).addTo(compositeDisposable = compositeDisposable)
    }


    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory

}