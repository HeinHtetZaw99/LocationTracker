package com.locationtracker.sources.cache.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbase.isNotEmptyString
import com.appbase.showLogE
import com.google.gson.annotations.SerializedName
import java.lang.Exception

@Entity(tableName = "contact_list")
class ContactVO {
    @SerializedName("State_Region")
    var stateRegion: String = ""

    @SerializedName("Organization")
    var organization: String = ""

    @SerializedName("Description")
    var description: String = ""

    @SerializedName("Township")
    var township: String = ""

    @SerializedName("Sector")
    var sector: String = ""

    @SerializedName("Tsp_Pcode")
    var tspPcode: String = ""

    @SerializedName("Contact_Secondary")
    var contactSecondary: String = ""

    @SerializedName("Contact_Primary")
    var contactPrimary: String = ""

    @SerializedName("Data_Submission_Time")
    var dataSubmissionTime: String = ""

    @PrimaryKey
    @SerializedName("Field_ID")
    var fieldID: Int = 0

    @SerializedName("Person")
    var person: String = ""

    @SerializedName("SR_Pcode")
    var sRPcode: String = ""


    fun getOccupation(): String {
        return if (description.isNotEmptyString())
            "$description , $organization \n$sector"
        else {
            if (person.isNotEmptyString())
                " $organization \n$sector"
            else
                sector
        }
    }

    fun getLocation() = "$township , $stateRegion"
    fun getPhoneNumber() = "$contactPrimary \n$contactSecondary"

    fun getDisplayName(): String {
        return if (person.isNotEmptyString())
            person
        else
            organization
    }

    fun getPhoneNumberList() : List<SettingsVO>{
        val phoneNumberList  = ArrayList<SettingsVO>()
        try {
            val contactPrimaryList = contactPrimary.split(",")
            contactPrimaryList.forEach {
                phoneNumberList.add(SettingsVO(it, it))
            }
            val contactSecondaryList = if(contactSecondary.isNotEmptyString()) contactSecondary.split(",") else emptyList()
            contactSecondaryList.forEach {
                phoneNumberList.add(SettingsVO(it, it))
            }
        }catch (e : Exception){
            showLogE("error in getPhoneNumberList",e)
        }
        return phoneNumberList
    }
}