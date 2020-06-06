package com.locationtracker

import com.appbase.mappers.UnidirectionalMap
import com.locationtracker.sources.cache.data.LocationEntity

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.OverlayItem
import javax.inject.Inject

class LocationOverlayItemConverter @Inject constructor() :
    UnidirectionalMap<LocationEntity, OverlayItem> {
    override fun map(data: LocationEntity): OverlayItem {
        return OverlayItem(
            data.timeStamp.toString(),
            data.getAddress(),
            data.startToEndTime,
            GeoPoint(data.latitude.toDouble(), data.longitude.toDouble())
        )
    }


}