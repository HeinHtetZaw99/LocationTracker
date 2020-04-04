package com.locationtracker.network.response

import retrofit2.http.Field

data class Address(
	var country: String? = null,
	@Field("county_code")
	var countryCode: String? = null,
	var road: String? = null,
	var city: String? = null,
	var county: String? = null,
	var postcode: String? = null,
	var suburb: String? = null,
	var state: String? = null
)
