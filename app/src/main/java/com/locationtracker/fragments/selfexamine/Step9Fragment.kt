package com.locationtracker.fragments.selfexamine

import androidx.fragment.app.Fragment
import android.view.View

import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step9.*

/**
 * A simple [Fragment] subclass.
 */
class Step9Fragment : BaseStepFragment<SelfExaminationActivity>() {

    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

    override var fragmentLayout: Int = R.layout.fragment_step9


    override fun saveData() {
        parentActivity.data.livingInTheHighlyInfectedArea =
            when(groupSelector.checkedRadioButtonId){
                R.id.yesBtn -> 1
                R.id.noBtn -> 2
                else -> -1
            }
    }

    override fun verify() {
        if (groupSelector.checkedRadioButtonId != -1) {
            parentActivity.goToNext()
        } else
            showErrorMessage()
    }
}
