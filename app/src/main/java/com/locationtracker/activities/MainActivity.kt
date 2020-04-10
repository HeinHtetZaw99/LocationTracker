package com.locationtracker.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes
import com.appbase.*
import com.appbase.activities.BaseActivity
import com.appbase.fragments.BaseFragment
import com.locationtracker.LocationTrackerApplication
import com.locationtracker.R
import com.locationtracker.background.LocationTrackerBroadcastReceiver
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity<MainViewModel>() {

    private val homeFragment = HomeFragment()
    private val historyFragment = HistoryFragment()
    private var activeFragment: BaseFragment = homeFragment
    private var lastVisitedFragment: Int = R.id.nav_home
    private var fragmentList = SparseArray<BaseFragment>().apply {
        put(R.id.nav_home, homeFragment)
        put(R.id.nav_history, historyFragment)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    override val layoutResId: Int = R.layout.activity_main

    override val rootLayout: View? by lazy { mainRootLayout }

    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {

    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }


    override fun initUI() {
        val logHeader =
            "\n---------------------------------------------\nWorkManger Deployed @ ${getTime()} ${getDate()}\n---------------------------------------------\n"
        writeFileToDisk("Android/locationData/", "log.txt", logHeader, false)
        initPeriodicWork()

        fragmentList.buildFragmentList(
            supportFragmentManager,
            R.id.fragmentContainer,
            R.id.nav_home
        )
        bottomNavigationBar.handleNavigationTransactions(fragmentList, ::makeFragmentTransaction)


        val receiver = ComponentName(this, LocationTrackerBroadcastReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun makeFragmentTransaction(
        currentFragment: BaseFragment,
        @IdRes selectedNavID: Int
    ) {
        lastVisitedFragment = selectedNavID
        supportFragmentManager.beginTransaction().hide(activeFragment)
            .show(currentFragment).commit()
        activeFragment = currentFragment
    }

    private fun initPeriodicWork() {
        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WorkManager.getInstance(this).enqueueUniqueWork(
                    "Main",
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequestBuilder<LocationTrackerWorker>().build()
                )
            } else {
                //todo haven't done yet
            }
    */
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            LocationTrackerApplication.BroadcastReceiverCode,
            Intent(this, LocationTrackerBroadcastReceiver::class.java),
            0
        )
        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager!!.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + 100, 180000,pendingIntent
            )
        } else {
     /*       alarmManager!![AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + 1000] = pendingIntent*/
            alarmManager!!.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + 100,180000,pendingIntent)
        }
    }

    override fun logOut() {

    }


    fun getHomeViewModel() = viewModel

}
