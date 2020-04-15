package com.locationtracker.fragments.selfexamine

import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step4.*
import kotlinx.android.synthetic.main.fragment_step5.*
import kotlinx.android.synthetic.main.fragment_step5.noSymptomsBtn

/**
 * A simple [Fragment] subclass.
 */
class Step5Fragment : BaseStepFragment<SelfExaminationActivity>() {

    private val checkListArray: ArrayList<CheckBox> by lazy {
        arrayListOf<CheckBox>(
            diabetesBtn,
            weakenHeartBtn,
            lowImmunityBtn,
            obesityBtn,
            lungRelatedDiseaseBtn,
            hypertensionBtn,
            underChemotherapyBtn,
            kidneyCleansingBtn,
            pregnancyBtn
        )
    }
    private val selectionList = ArrayList<Int>()

    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {
        /*
        android:id="@+id/diabetesBtn"

        android:id="@+id/weakenHeartBtn"

        android:id="@+id/lowImmunityBtn"

        android:id="@+id/obesityBtn"

        android:id="@+id/lungRelatedDiseaseBtn"

        android:id="@+id/hypertensionBtn"

        android:id="@+id/underChemotherapyBtn"

        android:id="@+id/kidneyCleansingBtn"

        android:id="@+id/pregnancyBtn"

        android:id="@+id/noSymptomsBtn" */
        /*    diabetesBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(1)
                } else
                    selectionList.remove(1)
            }

            weakenHeartBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(2)
                } else
                    selectionList.remove(2)
            }

            lowImmunityBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(3)
                } else
                    selectionList.remove(3)
            }

            obesityBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(4)
                } else
                    selectionList.remove(4)
            }

            lungRelatedDiseaseBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(5)
                } else
                    selectionList.remove(5)
            }

            hypertensionBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(6)
                } else
                    selectionList.remove(6)
            }
            underChemotherapyBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(7)
                } else
                    selectionList.remove(7)
            }

            kidneyCleansingBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(8)
                } else
                    selectionList.remove(8)
            }

            pregnancyBtn.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(9)
                } else
                    selectionList.remove(9)
            }
    */

        checkListArray.forEach {
            it.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(checkListArray.indexOf(it) + 1)
                } else
                    selectionList.remove(checkListArray.indexOf(it) + 1)
                noSymptomsBtn.isChecked  = false
                selectionList.remove(10)
            }
        }
        noSymptomsBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectionList.clear()
                selectionList.add(10)
                checkListArray.forEach {
                    it.isChecked = false
                }
            } else {
                selectionList.remove(10)
                noSymptomsBtn.isChecked = false
            }
        }
    }

    override var fragmentLayout: Int = R.layout.fragment_step5

    override fun saveData() {
        parentActivity.data.medicalBackgroundSelection.addAll(selectionList)
    }

    override fun verify() {
        if (selectionList.size > 0)
            parentActivity.goToNext()
        else
            showErrorMessage()
    }
}
