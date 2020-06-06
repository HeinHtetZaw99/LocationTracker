package com.locationtracker.di

import com.locationtracker.activities.ContactListActivity
import com.locationtracker.activities.MainActivity
import com.locationtracker.activities.MapActivity
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.fragments.selfexamine.*

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

    @ContributesAndroidInjector
    abstract fun mapActivity(): MapActivity

    @ContributesAndroidInjector
    abstract fun contactListActivity(): ContactListActivity

    @ContributesAndroidInjector
    abstract fun selfExaminationActivity(): SelfExaminationActivity

    @ContributesAndroidInjector
    abstract fun historyFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun homeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun introFragment(): IntroFragment

    @ContributesAndroidInjector
    abstract fun ageFragment(): AgeFragment

    @ContributesAndroidInjector
    abstract fun tempFragment(): TemperatureFragment

    @ContributesAndroidInjector
    abstract fun step2Fragment(): Step2Fragment

    @ContributesAndroidInjector
    abstract fun step3Fragment(): Step3Fragment

    @ContributesAndroidInjector
    abstract fun step4Fragment(): Step4Fragment

    @ContributesAndroidInjector
    abstract fun step5Fragment(): Step5Fragment

    @ContributesAndroidInjector
    abstract fun step6Fragment(): Step6Fragment

    @ContributesAndroidInjector
    abstract fun step7Fragment(): Step7Fragment

    @ContributesAndroidInjector
    abstract fun step8Fragment(): Step8Fragment

    @ContributesAndroidInjector
    abstract fun step9Fragment(): Step9Fragment

    @ContributesAndroidInjector
    abstract fun resultFragment(): ResultFragment
}
