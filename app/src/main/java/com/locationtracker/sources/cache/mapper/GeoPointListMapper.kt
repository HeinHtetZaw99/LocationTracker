package com.locationtracker.sources.cache.mapper

import com.appbase.mappers.UnidirectionalMap
import com.locationtracker.sources.cache.data.LocationEntity
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class GeoPointListMapper @Inject constructor(): UnidirectionalMap<List<LocationEntity>,List<GeoPoint>>{
    override fun map(data: List<LocationEntity>): List<GeoPoint> {
        return data.map {
            return@map GeoPoint(it.latitude.toDouble() , it.longitude.toDouble())
        }
    }

}