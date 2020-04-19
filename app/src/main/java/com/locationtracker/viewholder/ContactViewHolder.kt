package com.locationtracker.viewholder

import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.databinding.CardviewContactListBinding
import com.locationtracker.delegates.ContactDelegate
import com.locationtracker.sources.cache.data.ContactVO

class ContactViewHolder(
    private val binding: CardviewContactListBinding,
    private val delegate: ContactDelegate
) : BaseViewHolder<ContactVO>(binding.root) {
    override fun setData(data: ContactVO) {
        binding.contact = data
        binding.root.setOnClickListener { delegate.callPhone(data.getPhoneNumberList()) }
    }

}