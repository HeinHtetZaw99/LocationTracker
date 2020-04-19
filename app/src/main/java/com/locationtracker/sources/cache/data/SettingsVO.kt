package com.locationtracker.sources.cache.data

class SettingsVO(var settingsName: String, var settingsID: String) {


    companion object {

        fun getSettings(ids: Array<String>, settings: Array<String>): ArrayList<SettingsVO> {
            val settingsList = ArrayList<SettingsVO>()
            if (settings.size != ids.size)
                throw RuntimeException("Invalid Settings IDs and Labels")

            for (i in settings.indices) {
                settingsList.add(SettingsVO(settingsName = settings[i],settingsID = ids[i]))
            }
            return settingsList
        }
    }
}