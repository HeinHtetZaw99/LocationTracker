package com.locationtracker.sources

import com.appbase.showLogD
import com.locationtracker.sources.cache.AppDatabase
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO
import com.locationtracker.sources.cache.data.SettingsVO
import javax.inject.Inject

class ContactListDataSourceImpl @Inject constructor(
    private val dataBase: AppDatabase
) : ContactListDataSource {
    override fun saveContactList(contactList: List<ContactVO>) {
        dataBase.getContactDao().bulkInsert(contactList)
    }

    override fun getAllContactList(): List<ContactVO> {
        return dataBase.getContactDao().getAllContactList()
    }

    override fun getContactListByRegion(regionCode: String): List<ContactVO> {
        val data = dataBase.getContactDao().getContactListByRegion(regionCode)
        showLogD("data size got from getContactListByRegion(): ${data.size}")
        return data
    }

    override fun getFOCClinicListByRegion(regionCode: String): List<FocClinicVO> {
        val data = dataBase.getFocClinicDao().getClinicListByRegion(regionCode)
        showLogD("data size got from getFOCClinicListByRegion(): ${data.size}")
        return data
    }

    override fun getRegionsByCode(listType: ListType): List<SettingsVO> {
        val settingList = ArrayList<SettingsVO>()
        val codeList = if (listType is ListType.ContactList)
            dataBase.getContactDao().getAllRegionCodes()
        else dataBase.getFocClinicDao().getAllRegionCodes()
        codeList.forEach { code ->
            val regionName = dataBase.getContactDao().getRegionsNamesByRegionCode(code)
            settingList.add(SettingsVO(regionName, code))
        }
        return settingList
    }

    override fun saveClinicList(clinicList: List<FocClinicVO>) {
        dataBase.getFocClinicDao().bulkInsert(clinicList)
    }

}

sealed class ListType {
    object FOCClinics : ListType()
    object ContactList : ListType()
}