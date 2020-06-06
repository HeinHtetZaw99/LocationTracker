package com.locationtracker.sources.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.locationtracker.sources.cache.dao.ContactListDao
import com.locationtracker.sources.cache.dao.FocClinicsListDao
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.dao.LocationDao
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO

/**
 * This class is for generating of the database implementation by the framework
 * */
@Database(entities = [LocationEntity::class , ContactVO::class , FocClinicVO::class] , version = 2 ,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao() : LocationDao
    abstract fun getContactDao() : ContactListDao
    abstract fun getFocClinicDao() : FocClinicsListDao
}