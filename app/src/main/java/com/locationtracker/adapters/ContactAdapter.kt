package com.locationtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.appbase.adapters.BaseRecyclerAdapter
import com.locationtracker.R
import com.locationtracker.delegates.ContactDelegate
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.viewholder.ContactViewHolder

class ContactAdapter (private val context : Context) : BaseRecyclerAdapter<ContactVO,ContactViewHolder>(){
    private val inflater : LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            DataBindingUtil.inflate(inflater, R.layout.cardview_contact_list,parent,false), context as ContactDelegate
        )
    }

}