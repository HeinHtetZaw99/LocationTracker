package com.locationtracker.sources.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.google.android.gms.maps.model.LatLng
import com.locationtracker.sources.cache.data.LocationEntity

/**
 * This class is used to access data from the local database
 * for References , visit 'https://developer.android.com/training/data-storage/room/accessing-data'
 * */
@Dao
interface LocationDao : BaseDAO<LocationEntity> {
    @Query("select * from location_history")
    fun getAllLocationData(): List<LocationEntity>

   @Query("select * from location_history where latitude=:lat and longitude=:lng")
    fun getLocationByLatLng(lat : String , lng: String) : List<LocationEntity>

    @Query("select * from location_history where timeStamp =:timeStamp")
    fun getAllLocationData(timeStamp: String): List<LocationEntity>
}