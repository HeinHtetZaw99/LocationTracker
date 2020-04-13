package com.locationtracker.di

import android.content.Context
import com.appbase.components.exception.NetworkExceptionInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.locationtracker.BuildConfig
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module(includes = [NetworkModule.Providers::class , ServiceModule::class ,ServiceModule.Providers::class])
abstract class NetworkModule {

    @Module
    object Providers {
        @JvmStatic
        @Provides
        fun providesOkHttpClientBuilder(context : Context) : OkHttpClient.Builder {

            return OkHttpClient.Builder().apply {

                val loggerInterceptor = HttpLoggingInterceptor().apply {
                    level = when (BuildConfig.DEBUG) {
                        true  -> HttpLoggingInterceptor.Level.HEADERS
                        false -> HttpLoggingInterceptor.Level.NONE
                    }
                }

                addInterceptor(loggerInterceptor)
                    .addInterceptor(ChuckInterceptor(context))
                    .addInterceptor(NetworkExceptionInterceptor())

                    .readTimeout(8 , TimeUnit.SECONDS)
                    .writeTimeout(8 , TimeUnit.SECONDS)
                    .connectTimeout(8 , TimeUnit.SECONDS)
                    .cache(null)
            }
        }


        @JvmStatic
        @Provides
        @Named("primary")
        fun providesPrimaryRetrofitBuilder(gson : Gson) : Retrofit.Builder {
            return Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
        }



        @JvmStatic
        @Provides
        @Singleton
        fun gson(): Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient()
            .create()


    }
}