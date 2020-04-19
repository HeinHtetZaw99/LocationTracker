package com.locationtracker.sources.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.locationtracker.sources.cache.data.ContactVO

@Dao
interface ContactListDao : BaseDAO<ContactVO> {
    @Query("select * from contact_list")
    fun getAllContactList(): List<ContactVO>

    @Query("select * from contact_list where sRPcode =:regionCode")
    fun getContactListByRegion(regionCode: String): List<ContactVO>

    @Query("select distinct sRPcode from contact_list")
    fun getAllRegionCodes(): List<String>

    @Query("select stateRegion from contact_list where sRPcode =:regionCode")
    fun getRegionsNamesByRegionCode(regionCode: String): String
}