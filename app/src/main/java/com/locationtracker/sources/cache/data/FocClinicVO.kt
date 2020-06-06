package com.locationtracker.sources.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "clinic_list")
class FocClinicVO : BaseContactVO {
    @SerializedName("date")
    var date: String = ""

    @SerializedName("state_region")
    var stateRegion: String = ""

    @SerializedName("address")
    var address: String = ""

    @SerializedName("suburb")
    var suburb: String = ""

    @SerializedName("sr_pCode")
    var srPCode: String = ""

    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("time")
    var time: String = ""

    fun getDateTime() = "$date ,$time"
}
