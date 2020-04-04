package com.locationtracker.di

import android.content.Context
import androidx.room.Room
import com.appbase.components.SharePrefUtils
import com.appbase.di.BaseAppModule
import com.locationtracker.di.worker.WorkerAssistedInjectModule
import com.locationtracker.di.worker.WorkerBindingModule

import com.locationtracker.sources.cache.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        BaseAppModule::class, ActivityModule::class,
        ViewModelModule::class, AppModule.Provider::class,
        RepositoryModule::class, NetworkModule::class
    ]
)
abstract class AppModule {

    @Module
    object Provider {
        @Provides
        @JvmStatic
        @Singleton
        @Named("socket_url")
        fun providesURL() = "http://192.168.100.230:3000"

        @Provides
        @JvmStatic
        @Singleton
        fun providesSharePreference(context: Context) = SharePrefUtils(context)

        @Provides
        @JvmStatic
        @Singleton
        fun getDataBase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_offline_data_base.db"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }


    }
}
