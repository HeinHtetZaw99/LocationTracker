package com.locationtracker

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.fragment.app.Fragment
import androidx.work.Configuration
import androidx.work.WorkManager
import com.appbase.components.LoggingTree
import com.appbase.di.AppInjector
import com.appbase.showLogE
import com.locationtracker.di.AppComponent
import com.locationtracker.di.DaggerAppComponent
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class LocationTrackerApplication : Application(), HasActivityInjector  , HasSupportFragmentInjector, HasServiceInjector  {

    @Inject
    lateinit var dispatchingAndroidActivityInjector:
            DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidFragmentInjector:
            DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    private var appComponent: AppComponent? = null

    private fun getAppComponent(): AppComponent? {
        return appComponent ?: createAppComponent()
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidActivityInjector

    override fun onCreate() {
        super.onCreate()

        appComponent = getAppComponent()
        appComponent!!.inject(this)
        AppInjector.initAutoInjection(this)


        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(appComponent!!.factory()).build())
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

    companion object{
        val BroadcastReceiverCode = 234324243
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }



    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidFragmentInjector
    }

}