package com.locationtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.appbase.adapters.BaseRecyclerAdapter
import com.locationtracker.R
import com.locationtracker.sources.cache.data.AboutUsVO
import com.locationtracker.viewholder.AboutUsViewHolder

class AboutUsAdapter(private val context: Context) :
    BaseRecyclerAdapter<AboutUsVO, AboutUsViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutUsViewHolder {
        return AboutUsViewHolder(
            DataBindingUtil.inflate(inflater, R.layout.cardview_about_us, parent, false)
        )
    }

}