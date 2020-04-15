
package com.locationtracker.fragments.selfexamine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel

class IntroFragment : BaseStepFragment<SelfExaminationActivity>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intor, container, false)
    }

    override fun saveData() {
        
    }

    override fun verify() {
        parentActivity.goToNext()
    }

    override var fragmentLayout: Int = R.layout.fragment_intor
    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {
      
    }

    override fun loadData() {
      
    }

    override fun initViews(view: View) {
      
    }


}
