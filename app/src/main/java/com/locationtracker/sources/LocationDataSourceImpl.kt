package com.locationtracker.sources

import com.appbase.showLogD
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.network.service.ReverseGeocodeService
import com.locationtracker.sources.cache.AppDatabase
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import io.reactivex.Observable
import javax.inject.Inject


class LocationDataSourceImpl @Inject constructor(
    private val reverseGeocodeService: ReverseGeocodeService,
    private val locationEntityMapper: LocationEntityMapper,
    private val dataBase: AppDatabase
) : LocationDataSource {


    override fun getLocationListByDate(date: String): List<LocationEntity> {
        return dataBase.getLocationDao().getLocationListByDate(date)
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
    ): ReverseGeoEncodeResponse {
        return reverseGeocodeService.getReverseGeocodeObservable(lat, lng, "json").execute().body()!!
    }

    override fun addPartialLocation(data: LocationEntity) {
        dataBase.getLocationDao().insert(data)
    }

    override fun getAllLocationList(): List<LocationEntity> {
        return dataBase.getLocationDao().getAllLocationData()
    }

}