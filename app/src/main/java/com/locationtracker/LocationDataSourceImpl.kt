package com.locationtracker

import com.appbase.showLogD
import com.appbase.showLogE
import com.google.gson.Gson
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.network.service.ReverseGeocodeService
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.reactivex.Observable
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class LocationDataSourceImpl @Inject constructor(
    private val reverseGeocodeService: ReverseGeocodeService,
    private val locationEntityMapper: LocationEntityMapper
    ) : LocationDataSource{

    override fun getLocationListByDate(timeStamp: String): List<LocationEntity> {
        return emptyList()
    }

    override fun getReverseGeoEncodeData(lat: String, lng: String): Observable<ReverseGeoEncodeResponse> {
        return reverseGeocodeService.getReverseGeocode(lat ,lng ,"json")
    }

    private fun xmlToObj(xml : String) : ReverseGeoEncodeResponse{
        showLogD("data from network : $xml")
        val xmlToJson = XmlToJson.Builder(xml).build();
        var data : JSONObject? = null
        try {
            data  = xmlToJson.toJson()
        } catch (e: JSONException) {
            showLogE("JSON exception", e.message.toString())
            e.printStackTrace()
        }
        showLogD("XML", xml)

        showLogD("JSON", data.toString())
        return Gson().fromJson(data.toString(), ReverseGeoEncodeResponse::class.java)
    }
}