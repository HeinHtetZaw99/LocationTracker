package com.locationtracker

import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable

interface LocationRepository {
    fun getLocationListByDate(timeStamp : String) : Observable<List<LocationEntity>>
    fun getReverseGeoEncodeData(lat : String , lng : String) : Observable<ReverseGeoEncodeResponse>
}