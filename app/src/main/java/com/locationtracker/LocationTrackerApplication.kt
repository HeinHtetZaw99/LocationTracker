package com.locationtracker

import android.app.Activity
import android.app.Application
import com.appbase.components.LoggingTree
import com.appbase.di.AppInjector
import com.appbase.showLogE
import com.locationtracker.di.AppComponent
import com.locationtracker.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class LocationTrackerApplication : Application() , HasActivityInjector{

    @Inject
    lateinit var dispatchingAndroidActivityInjector:
            DispatchingAndroidInjector<Activity>

//    @Inject
//    lateinit var dispatchingAndroidFragmentInjector:
//            DispatchingAndroidInjector<android.app.Fragment>

    private var appComponent: AppComponent? = null

    private fun getAppComponent() : AppComponent? {
        return appComponent ?: createAppComponent()
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidActivityInjector

    override fun onCreate() {
        super.onCreate()

        appComponent = getAppComponent()
        appComponent!!.inject(this)
        AppInjector.initAutoInjection(this)
        Timber.plant(LoggingTree())
    }

    private fun createAppComponent(): AppComponent? {
        return DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    fun clearAppComponent() {
        showLogE("AppComponent has been slain! c(x_x)၁")
        appComponent = null
    }
}