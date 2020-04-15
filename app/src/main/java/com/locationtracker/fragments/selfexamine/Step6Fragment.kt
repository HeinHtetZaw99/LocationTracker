package com.locationtracker.fragments.selfexamine

import android.view.View
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step6.*

/**
 * A simple [Fragment] subclass.
 */
class Step6Fragment : BaseStepFragment<SelfExaminationActivity>() {

    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {

    }

    override var fragmentLayout: Int = R.layout.fragment_step6

    override fun saveData() {
        parentActivity.data.wentToCovidOutBreakPlacesSelection =
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
