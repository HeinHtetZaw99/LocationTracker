package com.locationtracker.sources.cache.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class is the table of the location database
 * for References , visit 'https://developer.android.com/training/data-storage/room/defining-data'
 * */
@Entity(tableName = "location_history")
class LocationEntity {
    @PrimaryKey
    @NonNull
    var timeStamp: String = ""
    var time: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var city: String = ""
    var road: String = ""
    var suburb: String = ""
    var county: String = ""
    var state: String = ""
    var postCode: String = ""
    var country: String = ""
    var dateTime: String = ""
    var retrievedBy: String = ""

    override fun toString(): String {
        return "LocationEntity(timeStamp='$timeStamp', latitude='$latitude', longitude='$longitude', city='$city', road='$road', suburb='$suburb', county='$county', state='$state', postCode='$postCode', country='$country', dateTime='$dateTime', retrievedBy='$retrievedBy')"
    }

    fun toTableString(): String {
        return "\n[$timeStamp\t\t|$latitude\t\t|$longitude\t\t|$city\t\t|$road\t\t|$suburb\t\t|$state\t\t|$postCode\t\t|$country\t\t|$dateTime\t\t|$time\t\t|$retrievedBy]\n"
    }



    companion object {
        fun toCSV(list: List<LocationEntity>): String {
            val sb = StringBuilder()
            list.forEach {
                sb.append(toCSVLine(it))
            }
            return sb.toString()
        }

        private fun toCSVLine(data: LocationEntity): String {
            return "\"${data.timeStamp}\",\"${data.time}\",\"${data.latitude}\",\"${data.longitude}\",\"${data.city}\",\"${data.road}\", \"${data.suburb}\",\"${data.county}\",\"${data.state}\",\"${data.postCode}\",\"${data.country}\",\"${data.dateTime}\"\n"
        }
    }
}