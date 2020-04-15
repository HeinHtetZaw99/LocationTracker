package com.locationtracker.fragments.selfexamine

import android.view.View
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step2.*

/**
 * A simple [Fragment] subclass.
 */
class Step2Fragment : BaseStepFragment<SelfExaminationActivity>() {


    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

    override var fragmentLayout: Int = R.layout.fragment_step2

    override fun saveData() {
        parentActivity.data.havingTroubleInBreathing =
            when (breathingDifficultySelector.checkedRadioButtonId) {
                R.id.choiceOne -> 1
                R.id.choiceTwo -> 2
                R.id.choiceThree -> 3
                else -> -1
            }
    }

    override fun verify() {
        if (breathingDifficultySelector.checkedRadioButtonId != -1) {
            parentActivity.goToNext()
        } else
            showErrorMessage()
    }

}
