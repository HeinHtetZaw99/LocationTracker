package com.locationtracker.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.appbase.adapters.BaseRecyclerAdapter
import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.R


import com.locationtracker.fragments.LocationHistoryFragment.OnListFragmentInteractionListener

import com.locationtracker.sources.cache.data.LocationEntity

import kotlinx.android.synthetic.main.fragment_location_history.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyLocationHistoryRecyclerViewAdapter(
    private val mListener: OnListFragmentInteractionListener?
) : BaseRecyclerAdapter<LocationEntity,MyLocationHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_history, parent, false)
        return ViewHolder(view,mListener!!)
    }


    inner class ViewHolder(val mView: View , private val listener: OnListFragmentInteractionListener) : BaseViewHolder<LocationEntity>(mView) {
        val mAddressView: TextView = mView.address
        val mTimeIntervalView: TextView = mView.time_interval
        override fun setData(data: LocationEntity) {
            mAddressView.text = data.getAddress()
            mTimeIntervalView.text = data.time
            mView.setOnClickListener { listener.onListFragmentInteraction(adapterPosition) }
        }
    }
}
