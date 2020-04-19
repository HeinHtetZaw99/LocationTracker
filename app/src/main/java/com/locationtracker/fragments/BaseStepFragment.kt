package com.locationtracker.fragments

import android.os.Bundle
import com.appbase.activities.BaseActivity
import com.appbase.fragments.BaseFragment
import com.appbase.models.vos.ReturnResult

abstract class BaseStepFragment<ParentActivity : BaseActivity<*>> : BaseFragment() {
    lateinit var parentActivity : ParentActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity!! as ParentActivity
    }

    abstract fun saveData()

    abstract fun verify()

    fun showErrorMessage(){
        parentActivity.showSnackBar(view!!, ReturnResult.ErrorResult("ကျေးဇူးပြု၍ တစ်ခုရွေးပေးပါ"))
    }

}