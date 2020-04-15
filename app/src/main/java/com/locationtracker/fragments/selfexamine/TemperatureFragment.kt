package com.locationtracker.fragments.selfexamine

import android.view.View
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_age.*
import kotlinx.android.synthetic.main.fragment_temperature.*

/**
 * A simple [Fragment] subclass.
 */
class TemperatureFragment : BaseStepFragment<SelfExaminationActivity>() {


    override fun saveData() {
        parentActivity.data.havingTempAbove38CSelection =
            when (temperatureSelector.checkedRadioButtonId) {
                R.id.yesBtn -> 1
                R.id.noBtn -> 2
                else -> -1
            }
    }

    override fun verify() {
        if (parentActivity.temperatureSelector.checkedRadioButtonId != -1)
            parentActivity.goToNext()
        else
            onError()
    }

    override var fragmentLayout: Int = R.layout.fragment_temperature

    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {
        showErrorMessage()
    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

}
