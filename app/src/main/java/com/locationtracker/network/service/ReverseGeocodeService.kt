package com.locationtracker.network.service


import com.locationtracker.network.response.ReverseGeoEncodeResponse
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReverseGeocodeService {
    @GET("/reverse")
    fun getReverseGeocodeObservable(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("format") format : String
    ): Call<ReverseGeoEncodeResponse>
}