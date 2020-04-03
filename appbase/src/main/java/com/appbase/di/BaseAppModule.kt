package com.appbase.di

import android.app.Application

import com.appbase.components.impls.GenericErrorMessageFactoryImpl
import com.appbase.components.impls.NetworkExceptionMessageFactoryImpl
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.components.interfaces.NetworkExceptionMessageFactory
import com.appbase.di.viewModel.ViewModelFactoryModule

import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [BaseAppModule.Provider::class, ViewModelFactoryModule::class])
abstract class BaseAppModule {


    @Binds
    abstract fun genericErrorMessageFactory(genericErrorMessageFactory: GenericErrorMessageFactoryImpl): GenericErrorMessageFactory

    @Binds
    abstract fun networkErrorMessageFactory(networkExceptionMessageFactory: NetworkExceptionMessageFactoryImpl): NetworkExceptionMessageFactory

    @Module
    object Provider {
        @Provides
        @JvmStatic
        @Singleton
        fun providesContext(application: Application) = application.applicationContext


    }

}