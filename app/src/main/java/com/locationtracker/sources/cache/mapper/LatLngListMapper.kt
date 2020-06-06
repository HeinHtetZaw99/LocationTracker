package com.locationtracker.sources.cache.mapper

import com.appbase.mappers.UnidirectionalMap
import com.google.android.gms.maps.model.LatLng
import com.locationtracker.sources.cache.data.LocationEntity
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class LatLngListMapper @Inject constructor(): UnidirectionalMap<List<LocationEntity>,List<LatLng>>{
    override fun map(data: List<LocationEntity>): List<LatLng> {
        return data.map {
            return@map LatLng(it.latitude.toDouble() , it.longitude.toDouble())
        }
    }

}