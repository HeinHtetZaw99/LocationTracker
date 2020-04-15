package com.locationtracker.fragments.selfexamine

import android.view.View
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_age.*

/**
 * A simple [Fragment] subclass.
 */
class AgeFragment : BaseStepFragment<SelfExaminationActivity>() {

    override fun saveData() {
        parentActivity.data.ageRangeSelection =
            when (ageGroupSelector.checkedRadioButtonId) {
                R.id.choiceOne -> 1
                R.id.choiceTwo -> 2
                R.id.choiceThree -> 3
                else -> -1
            }
    }

    override fun verify() {
        if (ageGroupSelector.checkedRadioButtonId != -1)
            parentActivity.goToNext()
        else
            onError()
    }

    override var fragmentLayout: Int = R.layout.fragment_age
    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {
        showErrorMessage()
    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

}
