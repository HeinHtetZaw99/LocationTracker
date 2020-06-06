package com.locationtracker.viewholder

import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.databinding.CardviewContactListBinding
import com.locationtracker.databinding.CardviewFocClinicBinding
import com.locationtracker.delegates.ContactDelegate
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO

class FOCClinicContactViewHolder(
    private val binding: CardviewFocClinicBinding,
    private val delegate: ContactDelegate
) : BaseViewHolder<FocClinicVO>(binding.root) {
    override fun setData(data: FocClinicVO) {
        binding.clinic = data
        binding.root.setOnClickListener {  }
    }

}