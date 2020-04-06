package com.locationtracker.sources

import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable

interface LocationDataSource {
    fun getAllLocationData() : List<LocationEntity>
    fun getLocationListByDate(timeStamp : String) : List<LocationEntity>
    fun addLocation(data : LocationEntity)
    fun getLocationDataByLatLong(lat: String , long : String ) : LocationEntity?
    fun getReverseGeoEncodeData(lat : String , lng : String) : Observable<ReverseGeoEncodeResponse>
}