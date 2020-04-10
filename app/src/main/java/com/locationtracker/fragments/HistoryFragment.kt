package com.locationtracker.fragments

import android.os.Bundle
import android.view.View
import com.appbase.fragments.BaseFragment
import com.appbase.showLongToast
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.viewmodels.MainViewModel
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarView
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import java.util.*


class HistoryFragment : BaseFragment() {
    override var fragmentLayout: Int = R.layout.fragment_history

    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onNetworkError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

}
