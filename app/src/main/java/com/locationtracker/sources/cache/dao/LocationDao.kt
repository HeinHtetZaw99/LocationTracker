package com.locationtracker.sources.cache.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.locationtracker.sources.cache.data.LocationEntity

/**
 * This class is used to access data from the local database
 * for References , visit 'https://developer.android.com/training/data-storage/room/accessing-data'
 * */
@Dao
interface LocationDao : BaseDAO<LocationEntity> {
    @Query("select * from location_history")
    fun getAllLocationData(): List<LocationEntity>

/*
    @Query("select * from location_history where latitude=:lat and longitude=:lng")
    fun getLocationByLatLng(lat: String, lng: String): List<LocationEntity>
*/

    @Query("select * from location_history where latitude like :lat || '%' and longitude like :lng || '%'")
    fun getLocationByLatLng(lat: String, lng: String): List<LocationEntity>

    @Query("select * from location_history where timeStamp =:timeStamp")
    fun getAllLocationData(timeStamp: Long): List<LocationEntity>

    @Query("select * from location_history where dateTime =:date")
    fun getLocationListByDate(date: String): List<LocationEntity>

/*    @Query("SELECT *  FROM location_history WHERE dateTime >= :startDate AND dateTime < :endDate")
    fun getLocationListByDate(startDate: String , endDate : String): List<LocationEntity>*/

    @Query("SELECT *  FROM location_history WHERE timeStamp between :startDate and :endDate")
    fun getLocationListByDate(startDate: Long , endDate : Long): List<LocationEntity>

    @Query("select * from location_history where city is null or city =\'\'")
    fun getUnCompleteLocationData(): List<LocationEntity>


}