package com.appbase.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appbase.activities.BaseActivity
import com.appbase.components.Connectivity
import com.pv.viewmodels.BaseViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
abstract class BaseFragment : Fragment() , HasSupportFragmentInjector   {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    abstract var fragmentLayout : Int

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(fragmentLayout , container , false)
    }


    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        initViews(view)
    }



    abstract val viewModel : BaseViewModel

    fun fetchDataFromSources() {
        if (Connectivity.isConnected(context!!)) {
            loadData()
        } else {
            onNetworkError()
        }
    }


    abstract fun onNetworkError()

    /** do your loading here only */
    abstract fun loadData()

    /** do your UI logics here only */
    abstract fun initViews(view  : View)

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}