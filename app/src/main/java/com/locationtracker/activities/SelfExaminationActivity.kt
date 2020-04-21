package com.locationtracker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.util.forEach
import com.appbase.activities.BaseActivity
import com.appbase.getKeyByValue
import com.appbase.setVisible
import com.appbase.showLogD
import com.appbase.showShortToast
import com.locationtracker.R
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.fragments.selfexamine.*
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_self_examination.*

class SelfExaminationActivity : BaseActivity<MainViewModel>() {

    val data = SelfExamineDataVO()
    private val resultFragment = ResultFragment()
    private val fragmentList = SparseArray<BaseStepFragment<SelfExaminationActivity>>().apply {
        put(0, IntroFragment())
        put(1, AgeFragment())
        put(2, TemperatureFragment())
        put(3, Step2Fragment())
        put(4, Step3Fragment())
        put(5, Step4Fragment())
        put(6, Step5Fragment())
        put(7, Step6Fragment())
        put(8, Step7Fragment())
        put(9, Step8Fragment())
        put(10, ResultFragment())
    }
    private var currentFragment: BaseStepFragment<SelfExaminationActivity> = fragmentList.get(0)
    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_self_examination)
        initUI()
    }


    override val layoutResId: Int = R.layout.activity_self_examination

    override val rootLayout: ViewGroup? by lazy { null }

    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {

    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }

    override fun initUI() {
        buildFragmentList()
        nextBtn.setOnClickListener { currentFragment.verify() }
        previousBtn.setOnClickListener { goToPrevious() }
    }

    override fun logOut() {

    }

    fun goToNext() {
        if (currentFragmentIndex < fragmentList.size()) {
            currentFragment.saveData()
            currentFragmentIndex++
            showFragment(fragmentList.get(currentFragmentIndex), false)
        } else {
            showShortToast(" ${data.getFinalizedResult()}")
            showLogD("final result : ${data.getFinalizedResult()}")
            //todo end of the fragment list. so may be it's time to call network calls ?
        }
    }

    fun hideNavigationBtns() {

        previousBtn.visibility = GONE
        nextBtn.visibility = GONE
    }

    fun goToPrevious() {
        if (currentFragmentIndex > 0) {
            currentFragment.saveData()
            currentFragmentIndex--
            showFragment(fragmentList.get(currentFragmentIndex), true)
        } else {
            super.onBackPressed()
        }
    }


    fun saveData() {

    }

    fun getMainViewModel() = viewModel

    private fun buildFragmentList() {
        fragmentList.forEach { key, fragment ->
            if (key == 0) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerLayout, fragment, key.toString())
                    .commitAllowingStateLoss()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerLayout, fragment, key.toString())
                    .hide(fragment)
                    .commitAllowingStateLoss()
            }


        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerLayout, resultFragment, "result")
            .hide(resultFragment)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        goToPrevious()
    }

    private fun showFragment(
        fragment: BaseStepFragment<SelfExaminationActivity>,
        reversed: Boolean
    ) {
        supportFragmentManager.beginTransaction()
            .addAnimation(reversed)
            .hide(currentFragment)
            .show(fragment)
            .commit()
        currentFragment = fragment
        currentFragmentIndex = fragmentList.getKeyByValue(fragment)

        if (currentFragment is ResultFragment) {
            hideNavigationBtns()
            (currentFragment as ResultFragment).reloadView()
        }
        if (currentFragment is IntroFragment) {
            previousBtn.setVisible(false)
        }else
            previousBtn.setVisible(true)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SelfExaminationActivity::class.java)
    }
}
