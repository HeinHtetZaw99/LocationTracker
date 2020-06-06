package com.locationtracker.repository

import com.locationtracker.sources.ContactListDataSource
import com.locationtracker.sources.ListType
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO
import com.locationtracker.sources.cache.data.SettingsVO
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class ContactListRepositoryImpl @Inject constructor(
    private val contactListDataSource: ContactListDataSource
) : ContactListRepository {
    override fun saveContactList(contactList: List<ContactVO>): Completable {
        return Completable.fromCallable { contactListDataSource.saveContactList(contactList) }
    }

    override fun saveClinicList(clinicList: List<FocClinicVO>): Completable {
        return Completable.fromCallable { contactListDataSource.saveClinicList(clinicList) }
    }

    override fun getAllContactList(): Observable<List<ContactVO>> {
        return Observable.fromCallable { contactListDataSource.getAllContactList() }
    }

    override fun getContactListByRegion(regionCode: String): Observable<List<ContactVO>> {
        return Observable.fromCallable { contactListDataSource.getContactListByRegion(regionCode) }
    }

    override fun getClinicListByRegion(regionCode: String): Observable<List<FocClinicVO>> {
        return Observable.fromCallable { contactListDataSource.getFOCClinicListByRegion(regionCode) }
    }

    override fun getSettingsList(listType: ListType): Observable<List<SettingsVO>> {
        return Observable.fromCallable { contactListDataSource.getRegionsByCode(listType) }
    }

}