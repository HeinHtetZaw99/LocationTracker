package com.locationtracker.fragments.selfexamine

import androidx.fragment.app.Fragment
import android.view.View

import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step2.*
import kotlinx.android.synthetic.main.fragment_step3.*

/**
 * A simple [Fragment] subclass.
 */
class Step3Fragment : BaseStepFragment<SelfExaminationActivity>() {


    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

    override var fragmentLayout: Int = R.layout.fragment_step3

    override fun saveData() {
        parentActivity.data.havingDryCoughSelection =
            when (dryCoughSelector.checkedRadioButtonId) {
                R.id.choiceOne -> 1
                R.id.choiceTwo -> 2
                R.id.choiceThree -> 3
                else -> -1
            }
    }

    override fun verify() {
        if (dryCoughSelector.checkedRadioButtonId != -1) {
            parentActivity.goToNext()
        } else
            showErrorMessage()
    }

}
