package com.locationtracker

import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationDataSource: LocationDataSource) :
    LocationRepository {
    override fun getLocationListByDate(timeStamp: String): Observable<List<LocationEntity>> {
        return Observable.fromCallable { locationDataSource.getLocationListByDate(timeStamp) }
    }

    override fun getReverseGeoEncodeData(
        lat: String,
        lng: String
    ): Observable<ReverseGeoEncodeResponse> {
        return locationDataSource.getReverseGeoEncodeData(lat, lng)
    }

}