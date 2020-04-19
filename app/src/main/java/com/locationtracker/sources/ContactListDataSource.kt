package com.locationtracker.sources

import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.SettingsVO

interface ContactListDataSource {
    fun saveContactList(contactList: List<ContactVO>)

    fun getAllContactList(): List<ContactVO>

    fun getContactListByRegion(regionCode: String): List<ContactVO>

    fun getRegionsByCode(): List<SettingsVO>

}