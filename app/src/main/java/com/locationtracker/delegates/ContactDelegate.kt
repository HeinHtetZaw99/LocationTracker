package com.locationtracker.delegates

import com.locationtracker.sources.cache.data.SettingsVO

interface ContactDelegate {
    fun callPhone(phoneNumberList: List<SettingsVO>)
}
