package com.locationtracker.viewholder

import android.view.View.GONE
import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.databinding.CardviewAboutUsBinding
import com.locationtracker.sources.cache.data.AboutUsVO

class AboutUsViewHolder(private val binding: CardviewAboutUsBinding) :
    BaseViewHolder<AboutUsVO>(binding.root) {
    override fun setData(data: AboutUsVO) {
        binding.data = data
        if (data.title.isEmpty())
            binding.costTv.visibility = GONE
    }

}