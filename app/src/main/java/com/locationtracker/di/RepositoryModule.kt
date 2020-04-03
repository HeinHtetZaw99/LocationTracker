package com.locationtracker.di


import com.locationtracker.LocationDataSource
import com.locationtracker.LocationDataSourceImpl
import com.locationtracker.LocationRepository
import com.locationtracker.LocationRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
      @Binds
      abstract fun getLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

      @Binds
      abstract fun getLocationDataSource(locationDataSourceImpl:  LocationDataSourceImpl): LocationDataSource


}