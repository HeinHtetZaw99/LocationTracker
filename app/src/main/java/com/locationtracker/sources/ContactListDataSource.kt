package com.locationtracker.sources

import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO
import com.locationtracker.sources.cache.data.SettingsVO

interface ContactListDataSource {
    fun saveContactList(contactList: List<ContactVO>)

    fun getAllContactList(): List<ContactVO>

    fun getContactListByRegion(regionCode: String): List<ContactVO>

    fun getFOCClinicListByRegion(regionCode: String): List<FocClinicVO>

    fun getRegionsByCode(listType: ListType): List<SettingsVO>

    fun saveClinicList(clinicList: List<FocClinicVO>)

}