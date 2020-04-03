package com.locationtracker.network.response

import retrofit2.http.Field

data class Address(
	val country: String? = null,
	@Field("county_code")
	val countryCode: String? = null,
	val road: String? = null,
	val city: String? = null,
	val county: String? = null,
	val postcode: String? = null,
	val suburb: String? = null,
	val state: String? = null
)
