package com.locationtracker

import com.locationtracker.viewholder.SettingsViewHolder

interface SettingsDelegate {
    fun onSettingsClicked(data : SettingsViewHolder.SettingType)
}
