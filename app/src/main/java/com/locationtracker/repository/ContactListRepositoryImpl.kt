package com.locationtracker.repository

import com.locationtracker.sources.ContactListDataSource
import com.locationtracker.sources.cache.data.ContactVO
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

    override fun getAllContactList(): Observable<List<ContactVO>> {
        return Observable.fromCallable { contactListDataSource.getAllContactList() }
    }

    override fun getContactListByRegion(regionCode: String): Observable<List<ContactVO>> {
        return Observable.fromCallable { contactListDataSource.getContactListByRegion(regionCode) }
    }

    override fun getSettingsList(): Observable<List<SettingsVO>> {
        return Observable.fromCallable { contactListDataSource.getRegionsByCode() }
    }

}