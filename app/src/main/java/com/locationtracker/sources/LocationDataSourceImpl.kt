package com.locationtracker.sources

import com.appbase.showLogD
import com.appbase.showLogE
import com.google.gson.Gson
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.network.service.ReverseGeocodeService
import com.locationtracker.sources.cache.AppDatabase
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import io.reactivex.Observable
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class LocationDataSourceImpl @Inject constructor(
    private val reverseGeocodeService: ReverseGeocodeService,
    private val locationEntityMapper: LocationEntityMapper,
    private val dataBase: AppDatabase
) : LocationDataSource {

    override fun getLocationListByDate(timeStamp: String): List<LocationEntity> {
        return emptyList()
    }

    override fun addLocation(data: LocationEntity) {
        dataBase.getLocationDao().insert(data)
    }

    override fun getLocationDataByLatLong(lat: String, long: String): LocationEntity? {
        val dataList = dataBase.getLocationDao().getLocationByLatLng(lat, long)
        showLogD("dataList from getLocationDataByLatLong($lat ,$long) : $dataList ")
        return if (dataList.isEmpty())
            null
        else
            dataList.last()
    }

    override fun getReverseGeoEncodeData(
        lat: String,
        lng: String
    ): Observable<ReverseGeoEncodeResponse> {
        return reverseGeocodeService.getReverseGeocodeObservable(lat, lng, "json")
    }

}