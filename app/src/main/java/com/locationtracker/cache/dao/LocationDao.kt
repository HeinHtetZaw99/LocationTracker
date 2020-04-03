package com.locationtracker.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.locationtracker.LocationEntity


@Dao
interface LocationDao : BaseDAO<LocationEntity> {
    @Query("select * from location_history")
    fun getAllLocationData(): List<LocationEntity>

    @Query("select * from location_history where timeStamp =:timeStamp")
    fun getAllLocationData(timeStamp: String): List<LocationEntity>
}