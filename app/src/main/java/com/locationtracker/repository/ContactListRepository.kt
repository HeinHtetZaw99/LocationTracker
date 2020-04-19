package com.locationtracker.repository

import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.SettingsVO
import io.reactivex.Completable
import io.reactivex.Observable

interface ContactListRepository {
    fun saveContactList(contactList: List<ContactVO>) : Completable

    fun getAllContactList(): Observable<List<ContactVO>>

    fun getContactListByRegion(regionCode: String): Observable<List<ContactVO>>

    fun getSettingsList(): Observable<List<SettingsVO>>
}