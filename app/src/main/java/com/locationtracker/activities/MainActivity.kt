package com.locationtracker.activities

import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appbase.activities.BaseActivity
import com.appbase.buildFragmentList
import com.appbase.components.SingleEventLiveData
import com.appbase.fragments.BaseFragment
import com.appbase.handleNavigationTransactions
import com.locationtracker.R
import com.locationtracker.background.LocationTrackerWorker
import com.locationtracker.fragments.HistoryFragment
import com.locationtracker.fragments.HomeFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration


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
        initPeriodicWork()
        fragmentList.buildFragmentList(
            supportFragmentManager,
            R.id.fragmentContainer,
            R.id.nav_home
        )
        bottomNavigationBar.handleNavigationTransactions(fragmentList, ::makeFragmentTransaction)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "Main",
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<LocationTrackerWorker>(Duration.ofMinutes(15)).build()
            )
        }else{
            //todo haven't done yet
        }
    }

    override fun logOut() {

    }

    /*private fun isLocationEnabled() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog = AlertDialog.Builder(this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent);
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog?.dismiss() }
            val alert = alertDialog.create();
            alert.show();
        } else {
            val alertDialog = AlertDialog.Builder(this);
            alertDialog.setTitle("Confirm Location");
            alertDialog.setMessage("Your Location is enabled, please enjoy");
            alertDialog.setNegativeButton("Back to interface") { dialog, which -> dialog?.dismiss() }

            val alert = alertDialog.create();
            alert.show();
        }
    }*/

    fun getHomeViewModel() = viewModel

}
