package com.locationtracker.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.appbase.fragments.BaseFragment
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.viewmodels.MainViewModel

class HistoryFragment : BaseFragment() {
    override var fragmentLayout: Int = R.layout.fragment_history
    
    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }

    override fun onNetworkError() {
        
    }

    override fun loadData() {
        
    }

    override fun initViews(view: View) {

        
    }

}
