package com.locationtracker.di

import android.app.Application
import com.locationtracker.LocationTrackerApplication
import com.locationtracker.di.worker.WorkerAssistedInjectModule
import com.locationtracker.di.worker.WorkerBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class,
        AndroidInjectionModule::class, WorkerBindingModule::class,
        WorkerAssistedInjectModule::class,
        AndroidSupportInjectionModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun factory(): AppWorkerFactory

    fun inject(application: LocationTrackerApplication)
}