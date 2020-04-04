package com.locationtracker.di.worker

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@Module(includes = [AssistedInject_WorkerAssistedInjectModule::class])
@AssistedModule
interface WorkerAssistedInjectModule
