package com.locationtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.appbase.adapters.BaseRecyclerAdapter
import com.locationtracker.R
import com.locationtracker.SettingsDelegate
import com.locationtracker.sources.cache.data.SettingsVO
import com.locationtracker.viewholder.SettingsViewHolder


class SettingsAdapter(
    private val context: Context,
    private val settingsDelegate: SettingsDelegate,
    private val type: SettingsViewHolder.SettingType
) : BaseRecyclerAdapter<SettingsVO, SettingsViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            DataBindingUtil.inflate(inflater, R.layout.cardview_settings, parent, false),
            settingsDelegate, type
        )
    }

}