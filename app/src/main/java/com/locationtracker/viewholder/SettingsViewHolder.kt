package com.locationtracker.viewholder

import com.appbase.viewholders.BaseViewHolder
import com.locationtracker.SettingsDelegate
import com.locationtracker.databinding.CardviewSettingsBinding
import com.locationtracker.sources.cache.data.SettingsVO


class SettingsViewHolder(
    private val binding: CardviewSettingsBinding,
    private val settingsDelegate: SettingsDelegate,
    private val type : SettingType
) :
    BaseViewHolder<SettingsVO>(binding.root) {
    override fun setData(data: SettingsVO) {
        binding.settings = data
        binding.settingsBtn.setOnClickListener {
            when(type)
            {
                is SettingType.Phone ->{
                    settingsDelegate.onSettingsClicked(SettingType.Phone(data.settingsName))
                }
                is SettingType.Region ->{
                    settingsDelegate.onSettingsClicked(SettingType.Region(adapterPosition))
                }
            }
         }
    }

    sealed class SettingType {
        class Phone(val phone: String) : SettingType()
        class Region(val adapterPosition: Int) : SettingType()
    }
}