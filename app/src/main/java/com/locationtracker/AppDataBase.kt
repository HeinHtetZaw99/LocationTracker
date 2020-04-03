package com.locationtracker

import androidx.room.Database
import androidx.room.RoomDatabase
import com.locationtracker.cache.dao.LocationDao

@Database(entities = [LocationEntity::class] , version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao() : LocationDao
}