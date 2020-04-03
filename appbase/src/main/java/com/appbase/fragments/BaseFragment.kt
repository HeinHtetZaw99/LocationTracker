package com.appbase.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.appbase.activities.BaseActivity
import com.appbase.components.Connectivity
import com.pv.viewmodels.BaseViewModel

@Suppress("UNCHECKED_CAST")
abstract class BaseFragment : Fragment() {

    abstract var fragmentLayout : Int

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
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
    abstract fun loadData()
    abstract fun initViews(view  : View)

}