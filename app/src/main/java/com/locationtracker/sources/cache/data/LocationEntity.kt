package com.locationtracker.sources.cache.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
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

    @Ignore
    var startToEndTime: String = ""

    override fun toString(): String {
        return "LocationEntity(timeStamp='$timeStamp', latitude='$latitude', longitude='$longitude', city='$city', road='$road', suburb='$suburb', county='$county', state='$state', postCode='$postCode', country='$country', dateTime='$dateTime', retrievedBy='$retrievedBy')"
    }

    fun toTableString(): String {
        return "\n[$timeStamp\t\t|$latitude\t\t|$longitude\t\t|$city\t\t|$road\t\t|$suburb\t\t|$state\t\t|$postCode\t\t|$country\t\t|$dateTime\t\t|$time\t\t|$retrievedBy]\n"
    }


    fun getAddress(): String {
       /* val address = "$road, $suburb, $county, $state ,$city"
        return if (address.replace(",", "").trim().isEmpty()) "Not Available" else address*/
        return "$latitude ,$longitude"
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


        fun convertForList(list: List<LocationEntity>): ArrayList<LocationEntity> {
            val convertedList = ArrayList<LocationEntity>()

            var previousAddress: LocationEntity? = null
            list.forEachIndexed { index, data ->
                if (index == 0)
                    previousAddress = data
                if (previousAddress == null)
                    previousAddress = data
                if (index == list.lastIndex && previousAddress != null){
                    previousAddress!!.startToEndTime = previousAddress!!.time + " to current"
                    convertedList.add(previousAddress!!)
                }

                    if (previousAddress!!.latitude == data.latitude && previousAddress!!.longitude == data.longitude) {
                        return@forEachIndexed
                    } else {
                        previousAddress!!.startToEndTime =
                            previousAddress!!.time + " to " + list[index - 1].time
                        convertedList.add(previousAddress!!)
                        previousAddress = null
                    }
            }
            return convertedList
        }
    }


}