package com.locationtracker.viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.locationtracker.R
import com.locationtracker.repository.LocationRepository
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import com.pv.viewmodels.BaseViewModel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val genericErrorMessageFactory: GenericErrorMessageFactory,
    private val locationEntityMapper: LocationEntityMapper
) : BaseViewModel() {

    val locationStatusLD: MutableLiveData<ReturnResult> by lazy { MutableLiveData<ReturnResult>() }
    val locationListLD: MutableLiveData<List<LocationEntity>> by lazy { MutableLiveData<List<LocationEntity>>() }

    fun getLocationHistory() {
        locationRepository.getAllLocationData().handle()
            .subscribe({
                locationListLD.postValue(it)
            }, {
                showLogE("Error in getGeoEncodeData()", it)
                locationStatusLD.postValue(
                    genericErrorMessageFactory.getError(
                        it,
                        R.string.error_empty_locatin_data_list
                    )
                )
            }).addTo(compositeDisposable)
    }

    fun getLocationHistoryByDate(date: String) {
        locationRepository.getLocationListByDate(date).handle()
            .subscribe({
                locationListLD.postValue(it)
            }, {
                showLogE("Error in getGeoEncodeData()", it)
                locationStatusLD.postValue(
                    genericErrorMessageFactory.getError(
                        it,
                        R.string.error_empty_locatin_data_list
                    )
                )
            }).addTo(compositeDisposable)
    }

    fun getGeoEncodeData(location: Location?) {
        if (location == null)
            locationStatusLD.postValue(ReturnResult.ErrorResult(R.string.error_in_getting_locatin_data))
        else
            locationRepository.getReverseGeoEncodeData(
                location.latitude.toString(),
                location.longitude.toString()
            )
                .handle()
                .subscribe({
                    val data = locationEntityMapper.map(it).apply {
                        retrievedBy = "User"
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                    locationStatusLD.postValue(ReturnResult.PositiveResult(data.toTableString()))
                    saveData(data)
                }, {
                    showLogE("Error in getGeoEncodeData()", it)
                    locationStatusLD.postValue(
                        genericErrorMessageFactory.getError(
                            it,
                            R.string.error_in_getting_locatin_data
                        )
                    )
                }).addTo(compositeDisposable)
    }

    fun saveData(data: LocationEntity) {
        locationRepository.addLocationRepository(data)
            .subscribe({
                showLogD("saved data : $data ")
            }, {
                showLogE("Error in saving to local db ", it)
            }).addTo(compositeDisposable)
    }

}

