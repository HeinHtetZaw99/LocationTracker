package com.locationtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.appbase.adapters.BaseRecyclerAdapter
import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.R
import com.locationtracker.delegates.ContactDelegate
import com.locationtracker.sources.cache.data.BaseContactVO
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.viewholder.ContactViewHolder
import com.locationtracker.viewholder.FOCClinicContactViewHolder

class ContactAdapter(private val context: Context) :
    BaseRecyclerAdapter<BaseContactVO, BaseViewHolder<BaseContactVO>>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BaseContactVO> {
        return if (viewType == R.layout.cardview_contact_list)
            ContactViewHolder(
                DataBindingUtil.inflate(inflater, viewType, parent, false),
                context as ContactDelegate
            ) as BaseViewHolder<BaseContactVO>
        else
            FOCClinicContactViewHolder(
                DataBindingUtil.inflate(inflater, viewType, parent, false),
                context as ContactDelegate
            ) as BaseViewHolder<BaseContactVO>
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList!![position] is ContactVO)
            R.layout.cardview_contact_list
        else R.layout.cardview_foc_clinic
    }

}