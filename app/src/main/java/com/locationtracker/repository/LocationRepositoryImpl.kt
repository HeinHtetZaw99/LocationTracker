package com.locationtracker.repository

import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.showLogD
import com.appbase.showLogE
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.sources.LocationDataSource
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import io.reactivex.Completable
import io.reactivex.Observable
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val locationEntityMapper: LocationEntityMapper,
    private val genericErrorMessageFactory: GenericErrorMessageFactory

) : LocationRepository {

    override fun getLocationListByDate(date: String): Observable<List<LocationEntity>> {
        return Observable.fromCallable { locationDataSource.getLocationListByDate(date) }
    }

    override fun addLocationRepository(data: LocationEntity): Completable {
        return Completable.fromCallable { locationDataSource.addLocation(data) }
    }

    override fun getReverseGeoEncodeData(
        lat: String,
        lng: String
    ): Observable<ReverseGeoEncodeResponse> {
        val cachedGeoEncodedData = locationDataSource.getLocationDataByLatLong(
            /*   formatTo3DecimalPlaces(lat.toDouble()),
               formatTo3DecimalPlaces(lng.toDouble())*/
            lat,
            lng
        )
        showLogD("Cache Reversed Geo Location : $cachedGeoEncodedData")
        return if (cachedGeoEncodedData == null) {
            showLogD("Cache Reversed Geo Location is null so retrieved from server")
            Observable.fromCallable {
                locationDataSource.getReverseGeoEncodeData(lat, lng)
            }.doOnError {
                showLogE(
                    "Error in getting reverse geo encode data ${genericErrorMessageFactory.getErrorMessage(
                        it
                    )}"
                )
            }
        } else {
            showLogD("Cache Reversed Geo Location is returned instead")
            Observable.fromCallable { locationEntityMapper.reverseMap(cachedGeoEncodedData) }
        }
    }

    override fun saveLatLngOnly(
        latitude: String,
        longitude: String,
        time: String,
        date: String
    ): Completable {
        return Completable.fromCallable {
            showLogD("partial location data saved : $latitude, $longitude , $time , $date")
            locationDataSource.addLocation(LocationEntity().apply {
                this.latitude = latitude
                this.longitude = longitude
                this.time = time
                this.dateTime = date
                this.timeStamp = Calendar.getInstance().timeInMillis.toString()
            })
        }
    }

    override fun getAllLocationData(): Observable<List<LocationEntity>> {
        return Observable.fromCallable { locationDataSource.getAllLocationList() }
    }

    private fun formatTo3DecimalPlaces(data: Double): String {
        return DecimalFormat("#.###").apply { roundingMode = RoundingMode.DOWN }.format(data)
    }


}