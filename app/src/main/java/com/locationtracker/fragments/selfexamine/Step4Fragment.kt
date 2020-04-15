package com.locationtracker.fragments.selfexamine

import android.view.View
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.locationtracker.R
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_step4.*

/**
 * A simple [Fragment] subclass.
 */
class Step4Fragment : BaseStepFragment<SelfExaminationActivity>() {

    private val checkListArray: ArrayList<CheckBox> by lazy {
        arrayListOf<CheckBox>(
            tirednessBtn,
            sneezingBtn,
            headacheBtn,
            soreThroatBtn,
            diaherreaBtn,
            vomitingBtn,
            noOdourBtn,
            noAppetiteBtn,
            bodyPainBtn,
            runnyNoseBtn
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
                android:id="@+id/tirednessBtn"

                android:id="@+id/sneezingBtn"

                android:id="@+id/headacheBtn"

                android:id="@+id/soreThroatBtn"

                android:id="@+id/diaherreaBtn"

                android:id="@+id/vomitingBtn"

                android:id="@+id/noOdourBtn"

                android:id="@+id/noAppetiteBtn"

                android:id="@+id/bodyPainBtn"

                android:id="@+id/runnyNoseBtn"

                android:id="@+id/noSymptomsBtn"

*/

        checkListArray.forEach {
            it.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectionList.add(checkListArray.indexOf(it) + 1)
                } else
                    selectionList.remove(checkListArray.indexOf(it) + 1)
                noSymptomsBtn.isChecked  = false
                selectionList.remove(11)
            }
        }
        /*  tirednessBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(1)
              } else
                  selectionList.remove(1)
          }

          sneezingBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(2)
              } else
                  selectionList.remove(2)
          }
          headacheBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(3)
              } else
                  selectionList.remove(3)
          }
          soreThroatBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(4)
              } else
                  selectionList.remove(4)
          }
          diaherreaBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(5)
              } else
                  selectionList.remove(5)
          }
          vomitingBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(6)
              } else
                  selectionList.remove(6)
          }
          noOdourBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(7)
              } else
                  selectionList.remove(7)
          }
          noAppetiteBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(8)
              } else
                  selectionList.remove(8)
          }
          bodyPainBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(9)
              } else
                  selectionList.remove(9)
          }
          runnyNoseBtn.setOnCheckedChangeListener { _, isChecked ->
              if (isChecked) {
                  selectionList.add(10)
              } else
                  selectionList.remove(10)
          }*/
        noSymptomsBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectionList.clear()
                selectionList.add(11)
                checkListArray.forEach {
                    it.isChecked = false
                }
            } else {
                selectionList.remove(11)
                noSymptomsBtn.isChecked = false
            }
        }
    }

    override var fragmentLayout: Int = R.layout.fragment_step4

    override fun saveData() {
        parentActivity.data.symptomSelection.addAll(selectionList)
    }

    override fun verify() {
        if (selectionList.size > 0)
            parentActivity.goToNext()
        else showErrorMessage()
    }
}
