package com.locationtracker.sources.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.FocClinicVO

@Dao
interface FocClinicsListDao : BaseDAO<FocClinicVO> {
    @Query("select * from clinic_list")
    fun getAllContactList(): List<FocClinicVO>

    @Query("select * from clinic_list where sRPcode =:regionCode")
    fun getClinicListByRegion(regionCode: String): List<FocClinicVO>

    @Query("select distinct srPcode from clinic_list")
    fun getAllRegionCodes(): List<String>

    @Query("select stateRegion from clinic_list where srPcode =:regionCode")
    fun getRegionsNamesByRegionCode(regionCode: String): String
}