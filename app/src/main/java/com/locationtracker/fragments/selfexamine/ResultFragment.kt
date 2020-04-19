package com.locationtracker.fragments.selfexamine

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appbase.setVisible
import com.appbase.show
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.activities.SelfExamineDataVO
import com.locationtracker.fragments.BaseStepFragment
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_result.*


/**
 * A simple [Fragment] subclass.
 */
class ResultFragment : BaseStepFragment<SelfExaminationActivity>() {


    override fun saveData() {

    }

    override fun verify() {

    }

    override var fragmentLayout: Int = R.layout.fragment_result

    override val viewModel: MainViewModel by lazy { parentActivity.getMainViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    fun reloadView(){
        when (parentActivity.data.getFinalizedResult()) {
            SelfExamineDataVO.FinalResult.COVID -> showCovidView()
            SelfExamineDataVO.FinalResult.SeasonalFlu
            -> showSeasonFluView()
            SelfExamineDataVO.FinalResult.HomeStay
            -> showHomeStayView()
        }
    }
    override fun initViews(view: View) {


    }

    private fun showSeasonFluView() {
        rootLayout.setBackgroundColor(
            ContextCompat.getColor(
                view!!.context,
                R.color.colorMediumDangerHint
            )
        )
        avatarIv.show(R.drawable.ic_stay_home)
        titleTv.text = "You are having some seasonal flu symptoms"
        subTitleTv.text =
            "Just stay at home and get rest. And please don't go outside unless it is totally necessary. \n If you ever thought you would need to see the doctors , please don't hesitate to contact to the local clinics"
        contactToDoctorsBtn.setOnClickListener { parentActivity.callToPhone("0150005") }
        goBackHomeBtn.setOnClickListener {
            startActivity(MainActivity.newIntent(parentActivity))
            parentActivity.finish()
        }

        contactToDoctorsBtn.setVisible(true)
        goBackHomeBtn.setVisible(true)
    }

    private fun showHomeStayView() {
        rootLayout.setBackgroundColor(
            ContextCompat.getColor(
                view!!.context,
                R.color.colorGoodHint
            )
        )
        avatarIv.show(R.drawable.ic_stay_home)
        titleTv.text = "သင့်တွင်မည်သည့်တုပ်ကွေးလက္ခဏာများမရှိပါ"
        subTitleTv.text =
            "သို့သော်ငြားလဲ ကျေးဇူးပြု၍မလိုအပ်ပဲ အပြင်ထွက်ခြင်းကိုရှောင်ရှားပေးပါ။ သင်ချစ်ရသောမိသားစုများနှင့် အိမ်ထဲတွင်အနားယူပါ။"
        contactToDoctorsBtn.setOnClickListener { parentActivity.callToPhone("2019") }
        goBackHomeBtn.setOnClickListener {
            startActivity(MainActivity.newIntent(parentActivity))
            parentActivity.finish()
        }

        contactToDoctorsBtn.setVisible(false)
        goBackHomeBtn.setVisible(true)
    }

    private fun showCovidView() {
        rootLayout.setBackgroundColor(
            ContextCompat.getColor(
                view!!.context,
                R.color.colorDangerHint
            )
        )
        avatarIv.show(R.drawable.ic_go_to_hospital)
        titleTv.text = "သင့်တွင်COVID-19 နှင့်အလားတူသောလက္ခဏာများဖြစ်ပွားနေသည်ဟု သံသယရှိမိပါသည်"
        subTitleTv.text =
            "ခြောက်ခြားတုန်လှုပ်ခြင်းမပြုပဲ အာဏာပိုင်များနှင့် ဆေးဝန်ထမ်းများ၏ကုသမှုကိုခံယူပေးပါ။ ကျေးဇူးပြု၍လောလောဆယ် အပြင်မထွက်ပဲ အခြားသူများနှင့်ခပ်ခွာခွာနေပေးပါ။"
        contactToDoctorsBtn.setOnClickListener { parentActivity.callToPhone("2019") }
        goBackHomeBtn.setOnClickListener {
            startActivity(MainActivity.newIntent(parentActivity))
            parentActivity.finish()
        }

        contactToDoctorsBtn.setVisible(true)
        goBackHomeBtn.setVisible(true)
    }



}
