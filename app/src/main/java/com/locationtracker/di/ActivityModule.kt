package com.locationtracker.di

import com.locationtracker.activities.MainActivity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    //todo manual : In case of declaring one new activity  please declare that acitiviy as the following methods
    /**
     *  @ContributesAndroidInjector
        abstract fun mainActivity(): MainActivity

        Method name doesn't matter, the return type must be the activity u have declared
     */

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity


}
