package com.locationtracker.di.worker

import com.locationtracker.background.LocationTrackerWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(LocationTrackerWorker::class)
    fun bindLocationTrackerWorker(factory: LocationTrackerWorker.Factory): ChildWorkerFactory
}