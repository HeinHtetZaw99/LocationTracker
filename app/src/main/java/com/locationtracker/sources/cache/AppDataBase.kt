package com.locationtracker.sources.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.locationtracker.sources.cache.dao.ContactListDao
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.dao.LocationDao
import com.locationtracker.sources.cache.data.ContactVO

/**
 * This class is for generating of the database implementation by the framework
 * */
@Database(entities = [LocationEntity::class , ContactVO::class] , version = 2 ,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao() : LocationDao
    abstract fun getContactDao() : ContactListDao
}