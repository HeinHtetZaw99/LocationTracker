package com.locationtracker.repository

import androidx.lifecycle.LiveData
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Completable
import io.reactivex.Observable

interface LocationRepository {
    fun getAllLocationData() : Observable<List<LocationEntity>>
    fun getLocationListByDate(startDate : String, endDate : String ) :Observable<List<LocationEntity>>
    fun addLocationRepository(data: LocationEntity) : Completable
    fun getReverseGeoEncodeData(lat : String , lng : String) : Observable<ReverseGeoEncodeResponse>
    fun saveLatLngOnly(latitude: String, longitude: String, time: String, date: String) : Completable

}