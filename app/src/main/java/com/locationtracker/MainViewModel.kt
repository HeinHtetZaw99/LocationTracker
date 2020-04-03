package com.locationtracker

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogE
import com.pv.viewmodels.BaseViewModel
import java.lang.RuntimeException
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val genericErrorMessageFactory: GenericErrorMessageFactory,
    private val locationEntityMapper: LocationEntityMapper
) : BaseViewModel() {

    val locationStatusLD: MutableLiveData<ReturnResult> by lazy { MutableLiveData<ReturnResult>() }
    fun getGeoEncodeData(location: Location?) {
        if (location == null)
            locationStatusLD.postValue(ReturnResult.ErrorResult(R.string.error_in_getting_locatin_data))
        else
            locationRepository.getReverseGeoEncodeData(
                location.latitude.toString(),
                location.longitude.toString()
            )
                .handle().subscribe({
                    locationStatusLD.postValue(ReturnResult.PositiveResult(locationEntityMapper.map(it).toString()))
                }, {
                    showLogE("Error in getGeoEncodeData()",it)
                    locationStatusLD.postValue(
                        genericErrorMessageFactory.getError(
                            it,
                            R.string.error_in_getting_locatin_data
                        )
                    )
                })
    }

}

