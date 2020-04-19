package com.locationtracker.di

import androidx.annotation.NonNull
import com.locationtracker.background.LocationTrackerService
import com.locationtracker.background.LostLocationService
import com.locationtracker.network.service.ReverseGeocodeService
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import retrofit2.Retrofit
import javax.inject.Named

@Module(includes = [ServiceModule.Providers::class])
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun contributeLocationTrackerService(): LocationTrackerService

    @ContributesAndroidInjector
    abstract fun contributeLostLocationService(): LostLocationService

    @Module
    object Providers {

        @JvmStatic
        @NonNull
        @Provides
        fun provideReverseGeoEncodingService(@Named("primary") retrofitBuilder: Retrofit.Builder): ReverseGeocodeService {
            return retrofitBuilder.build().create(ReverseGeocodeService::class.java)
        }


    }
}