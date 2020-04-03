package com.locationtracker.network.response

import retrofit2.http.Field

data class ReverseGeoEncodeResponse(
	@Field("osm_type")
	val osmType: String? = null,
	@Field("osm_id")
	val osmId: Long = 0,
	val licence: String? = null,
	val boundingbox: List<String?>? = null,
	val address: Address? = null,
	val lon: String? = null,
	@Field("display_name")
	val displayName: String? = null,
	@Field("place_id")
	val placeId: Long = 0,
	val lat: String? = null
)
