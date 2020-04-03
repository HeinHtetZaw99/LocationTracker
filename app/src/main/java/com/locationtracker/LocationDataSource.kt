package com.locationtracker

import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable


interface LocationDataSource {
    fun getLocationListByDate(timeStamp : String) : List<LocationEntity>
    fun getReverseGeoEncodeData(lat : String , lng : String) : Observable<ReverseGeoEncodeResponse>
}