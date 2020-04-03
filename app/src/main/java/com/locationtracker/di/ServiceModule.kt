package com.locationtracker.di

import androidx.annotation.NonNull
import com.appbase.showLogD
import com.locationtracker.network.service.ReverseGeocodeService
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named

@Module(includes = [ServiceModule.Providers::class])
abstract class ServiceModule {

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