package com.locationtracker.network.response

import retrofit2.http.Field

data class ReverseGeoEncodeResponse(
	@Field("osm_type")
	var osmType: String? = null,
	@Field("osm_id")
	var osmId: Long = 0,
	var licence: String? = null,
	var boundingbox: List<String?>? = null,
	var address: Address? = null,
	var lon: String? = null,
	@Field("display_name")
	var displayName: String? = null,
	@Field("place_id")
	var placeId: Long = 0,
	var lat: String? = null
)
