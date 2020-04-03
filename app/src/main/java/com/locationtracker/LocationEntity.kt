package com.locationtracker

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_history")
class LocationEntity {
    @PrimaryKey
    @NonNull
    var timeStamp : String =""
    var lat : String = ""
    var long : String = ""
    var address : String = ""
    var city : String = ""
    var road : String =""
    var suburb : String =""
    var county : String =""
    var state : String = ""
    var postCode : String = ""
    var country : String = ""
    var dateTime : String = ""
    override fun toString(): String {
        return "LocationEntity(timeStamp='$timeStamp', lat='$lat', long='$long', address='$address', city='$city', road='$road', suburb='$suburb', county='$county', state='$state', postCode='$postCode', country='$country', dateTime='$dateTime')"
    }

}