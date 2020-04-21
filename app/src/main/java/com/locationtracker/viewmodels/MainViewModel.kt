package com.locationtracker.viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.appbase.components.SingleEventLiveData
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.locationtracker.R
import com.locationtracker.repository.ContactListRepository
import com.locationtracker.repository.LocationRepository
import com.locationtracker.sources.cache.data.BannerVO
import com.locationtracker.sources.cache.data.ContactVO
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.data.SettingsVO
import com.locationtracker.sources.cache.mapper.LocationEntityMapper
import com.pv.viewmodels.BaseViewModel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val genericErrorMessageFactory: GenericErrorMessageFactory,
    private val contactListRepository: ContactListRepository,
    private val locationEntityMapper: LocationEntityMapper
) : BaseViewModel() {

    private val bannerList = ArrayList<BannerVO>().apply {
        add(BannerVO("အိမ်တွင်သာနေထိုင်ပါ"))
        add(BannerVO("အခြားသူများနှင့်အနဲဆုံး ၆ပေအကွာနေပါ"))
        add(BannerVO("လက်ကိုဆပ်ပြာဖြင့်သေချာစွာမကြာခဏဆေးကြောပါ"))
        add(BannerVO("နှာစေးချောင်းဆိုးသည့်အခါ လက်ကိုင်ပဝါဖြင့်သေချာအုပ်ပါ"))
        add(BannerVO("ဖျားနာလျှင် face mask တပ်ထားပါ"))
        add(BannerVO("ဖျားနာလျှင် နီးစပ်ရာဆေးခန်းကိုဆက်သွယ်ပါ"))

    }
    val locationStatusLD: SingleEventLiveData<ReturnResult> by lazy { SingleEventLiveData<ReturnResult>() }
    val locationHistoryStatusLD: SingleEventLiveData<ReturnResult> by lazy { SingleEventLiveData<ReturnResult>() }
    val locationListLD: MutableLiveData<List<LocationEntity>> by lazy { MutableLiveData<List<LocationEntity>>() }

    val contactListLD: MutableLiveData<List<ContactVO>> by lazy { MutableLiveData<List<ContactVO>>() }
    val settingsListLD: MutableLiveData<List<SettingsVO>> by lazy { MutableLiveData<List<SettingsVO>>() }

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
                locationHistoryStatusLD.postValue(
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


    fun saveContactListToDB(contactList: List<ContactVO>) {
        contactListRepository.saveContactList(contactList).subscribe {
            showLogD("contactList : ${contactList.size} saved")
        }.addTo(compositeDisposable)
    }

    fun getRegionContactList(regionCode: String) {
        contactListRepository.getContactListByRegion(regionCode)
            .handle().subscribe({
                contactListLD.postValue(it)
            }, {
                showLogE("Error in saving contact List to local db ", it)
            }).addTo(compositeDisposable)
    }

    fun getSettingsList() {
        contactListRepository.getSettingsList().handle()
            .subscribe({
                settingsListLD.postValue(it)
            }, {
                showLogE("Error in getting settingsList from  local db ", it)
            })
            .addTo(compositeDisposable)
    }

    fun loadBannerList() = bannerList


}

