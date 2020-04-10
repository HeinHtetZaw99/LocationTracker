package com.locationtracker.sources.cache.mapper

import com.appbase.getDate
import com.appbase.getTime
import com.appbase.mappers.BidirectionalMap
import com.locationtracker.network.response.Address
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.sources.cache.data.LocationEntity
import java.util.*
import javax.inject.Inject

/**
 * This is where the conversion from network data type to entity and vice versa*/
class LocationEntityMapper @Inject constructor() :
    BidirectionalMap<ReverseGeoEncodeResponse, LocationEntity> {
    override fun map(data: ReverseGeoEncodeResponse): LocationEntity {
        return LocationEntity().apply {
            timeStamp = Calendar.getInstance().timeInMillis.toString()
            time = getTime()
            latitude = data.lat ?: ""
            longitude = data.lon ?: ""
            city = data.address?.city ?: ""
            road = data.address?.road ?: ""
            suburb = data.address?.suburb ?: ""
            county = data.address?.county ?: ""
            state = data.address?.state ?: ""
            postCode = data.address?.postcode ?: ""
            country = data.address?.country ?: ""
            dateTime = getDate()
        }
    }

    override fun reverseMap(data: LocationEntity): ReverseGeoEncodeResponse {
        return ReverseGeoEncodeResponse().apply {
            address = Address(
                country = data.country,
                road = data.road,
                city = data.city,
                county = data.county,
                postcode = data.postCode,
                suburb = data.suburb,
                state = data.state
            )
            lon = null
            displayName = null
            lat = null
        }
    }

}