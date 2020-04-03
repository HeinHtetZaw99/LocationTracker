package com.locationtracker

import com.appbase.mappers.UnidirectionalMap
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import javax.inject.Inject

class LocationEntityMapper @Inject constructor() : UnidirectionalMap<ReverseGeoEncodeResponse, LocationEntity>{
    override fun map(data: ReverseGeoEncodeResponse): LocationEntity {
        return LocationEntity().apply {
             timeStamp  = ""
             lat  = ""
             long  = ""
             address  = ""
             city  = data.address?.city ?: ""
             road  =data.address?.road ?: ""
             suburb  =data.address?.suburb?: ""
             county  =data.address?.county?: ""
             state  = data.address?.state?: ""
             postCode  = data.address?.postcode?: ""
             country  = data.address?.country?: ""
             dateTime  = ""
        }
    }

}