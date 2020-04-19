package com.locationtracker.sources

import androidx.lifecycle.LiveData
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable

interface LocationDataSource {

    fun getLocationListByDate(date : String) : List<LocationEntity>

    fun addLocation(data : LocationEntity)
    fun getLocationDataByLatLong(lat: String , long : String ) : LocationEntity?
    fun getReverseGeoEncodeData(lat : String , lng : String) : ReverseGeoEncodeResponse
    fun addPartialLocation(data: LocationEntity)
    fun getAllLocationList() : List<LocationEntity>
}