package com.locationtracker.di


import com.locationtracker.repository.ContactListRepository
import com.locationtracker.repository.ContactListRepositoryImpl
import com.locationtracker.sources.LocationDataSource
import com.locationtracker.sources.LocationDataSourceImpl
import com.locationtracker.repository.LocationRepository
import com.locationtracker.repository.LocationRepositoryImpl
import com.locationtracker.sources.ContactListDataSource
import com.locationtracker.sources.ContactListDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
      @Binds
      abstract fun getLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

      @Binds
      abstract fun getLocationDataSource(locationDataSourceImpl: LocationDataSourceImpl): LocationDataSource

      @Binds
      abstract fun getContactListRepository(contactListRepository: ContactListRepositoryImpl): ContactListRepository

      @Binds
      abstract fun getContactListDataSource(contactListDataSource: ContactListDataSourceImpl): ContactListDataSource


}