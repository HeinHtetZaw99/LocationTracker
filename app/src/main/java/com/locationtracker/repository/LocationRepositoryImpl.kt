package com.locationtracker.repository

import com.appbase.showLogD
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.sources.LocationDataSource
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val locationEntityMapper: LocationEntityMapper

) :
    LocationRepository {
    override fun getLocationListByDate(timeStamp: String): Observable<List<LocationEntity>> {
        return Observable.fromCallable { locationDataSource.getLocationListByDate(timeStamp) }
    }

    override fun addLocationRepository(data: LocationEntity): Completable {
        return Completable.fromCallable { locationDataSource.addLocation(data) }
    }

    override fun getReverseGeoEncodeData(
        lat: String,
        lng: String
    ): Observable<ReverseGeoEncodeResponse> {
        val cachedGeoEncodedData = locationDataSource.getLocationDataByLatLong(lat, lng)
        showLogD("Cache Reversed Geo Location : $cachedGeoEncodedData")
        return if (cachedGeoEncodedData == null) {
            showLogD("Cache Reversed Geo Location is null so retrieved from server")
            locationDataSource.getReverseGeoEncodeData(lat, lng)
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
            locationDataSource.addPartialLocation(LocationEntity().apply {
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

}