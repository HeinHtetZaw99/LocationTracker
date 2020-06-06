package com.locationtracker.sources

import com.appbase.getDateFormatter
import com.appbase.parseDate
import com.appbase.showLogD
import com.appbase.showLogE
import com.locationtracker.network.response.ReverseGeoEncodeResponse
import com.locationtracker.network.service.ReverseGeocodeService
import com.locationtracker.sources.cache.AppDatabase
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class LocationDataSourceImpl @Inject constructor(
    private val reverseGeocodeService: ReverseGeocodeService,
    private val locationEntityMapper: LocationEntityMapper,
    private val dataBase: AppDatabase
) : LocationDataSource {

    //an example of shit code xD will change to more efficient one later
    override fun getLocationListByDate(startDate: String, endDate: String): List<LocationEntity> {
        showLogE("getLocationListByDate($startDate,$endDate)")
        return if (startDate == endDate)
            dataBase.getLocationDao().getLocationListByDate(startDate)
        else {
            /*         val startDateEntry = parseDate(startDate)
                val endDateEntry = parseDate(endDate)
                showLogE("getLocationListByDate($startDate,$endDate) with $startDateEntry and $endDateEntry")
                dataBase.getLocationDao()
                    .getLocationListByDate(startDateEntry, endDateEntry)*/
            val dataList = ArrayList<LocationEntity>()
            val dateRangeList = getDatesBetween(parseDate(startDate) , parseDate(endDate))
            showLogE("dateRange to be fetched $dateRangeList")
            dateRangeList.forEach {
                dataList.addAll(dataBase.getLocationDao().getLocationListByDate(it))
            }
            dataList
        }
    }

    override fun addLocation(data: LocationEntity) {
        dataBase.getLocationDao().insert(data)
    }

    override fun getLocationDataByLatLong(lat: String, long: String): LocationEntity? {
        val dataList = dataBase.getLocationDao().getLocationByLatLng(lat, long)
        showLogD("dataList from getLocationDataByLatLong($lat ,$long) : ${dataList.size} ")
        return if (dataList.isEmpty())
            null
        else
            dataList.last()
    }

    override fun getReverseGeoEncodeData(
        lat: String,
        lng: String
    ): ReverseGeoEncodeResponse {
        return reverseGeocodeService.getReverseGeocodeObservable(lat, lng, "json").execute()
            .body()!!
    }

    override fun addPartialLocation(data: LocationEntity) {
        dataBase.getLocationDao().insert(data)
    }

    override fun getAllLocationList(): List<LocationEntity> {
        return dataBase.getLocationDao().getAllLocationData()
    }


    fun getDatesBetween(
        startDate: Date, endDate: Date
    ): List<String> {
        val datesInRange: MutableList<String> = ArrayList()
        val calendar: Calendar = GregorianCalendar()
        calendar.time = startDate
        val endCalendar: Calendar = GregorianCalendar()
        endCalendar.time = endDate

        while (calendar.before(endCalendar)) {
            val result: Date = calendar.time
            datesInRange.add(getDateFormatter().format(result.time))
            calendar.add(Calendar.DATE, 1)
        }
        datesInRange.add(getDateFormatter().format(endDate.time))
        return datesInRange
    }
}