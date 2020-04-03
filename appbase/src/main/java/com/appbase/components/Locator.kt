package com.appbase.components

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.appbase.components.Connectivity.isConnected
import com.appbase.components.Connectivity.isConnectedMobile
import com.appbase.showLogD


/**
 * Get device location using various methods
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
class Locator(private val context: Context, private val locationManager: LocationManager) :
    LocationListener {
    private val LOG_TAG = "locator"
    private val TIME_INTERVAL = 100 // minimum time between updates in milliseconds
    private val DISTANCE_INTERVAL = 1 // minimum distance between updates in meters
    private var method: Method? = null
    private var callback: Listener? = null

    @SuppressLint("MissingPermission")
    fun getLocation(
        method: Method?,
        callback: Listener?
    ) {
        this.method = method
        this.callback = callback
        when (this.method) {
            Method.NETWORK, Method.NETWORK_THEN_GPS -> {
                @SuppressLint("MissingPermission") val networkLocation =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null) {
                    showLogD("$LOG_TAG Last known location found for network provider : $networkLocation")
                    this.callback!!.onLocationFound(networkLocation)
                } else {
                    showLogD("$LOG_TAG Request updates from network provider.")
                    requestUpdates(LocationManager.NETWORK_PROVIDER)
                }
            }
            Method.GPS -> {
                showLogD("Location is being tracked with gps")
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000L,
                    10f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            if (location != null) {
                                showLogD(
                                    LOG_TAG,
                                    "Last known location found for GPS provider : $location"
                                )
                                callback!!.onLocationFound(location)
                            } else {
                                showLogD(LOG_TAG, "Request updates from GPS provider.")
                                requestUpdates(LocationManager.GPS_PROVIDER)
                            }
                        }

                        override fun onStatusChanged(
                            provider: String?,
                            status: Int,
                            extras: Bundle?
                        ) {
                            showLogD(
                                LOG_TAG,
                                "Locator.onStatusChanged(provider: $provider, status: $status, extras: $extras)"
                            )
                        }

                        override fun onProviderEnabled(provider: String?) {
                            showLogD(LOG_TAG, "Locator.onProviderEnabled(provider: $provider")
                        }

                        override fun onProviderDisabled(provider: String?) {
                            showLogD(LOG_TAG, "Locator.onProviderDisabled(provider: $provider")
                        }

                    },
                    null
                );

            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)
                && isConnected(context)
            ) {
                showLogD("$LOG_TAG Network connected, start listening : $provider")
                locationManager.requestLocationUpdates(
                    provider,
                    TIME_INTERVAL.toLong(),
                    DISTANCE_INTERVAL.toFloat(),
                    this
                )
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)
                && isConnectedMobile(context)
            ) {
                showLogD("$LOG_TAG Mobile network connected, start listening : $provider")
                locationManager.requestLocationUpdates(
                    provider,
                    TIME_INTERVAL.toLong(),
                    DISTANCE_INTERVAL.toFloat(),
                    this
                )
            } else {
                showLogD("$LOG_TAG Proper network not connected for provider : $provider")
                onProviderDisabled(provider)
            }
        } else {
            onProviderDisabled(provider)
        }
    }

    fun cancel() {
        showLogD("$LOG_TAG Locating canceled.")
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        showLogD(LOG_TAG + " Location found : " + location.latitude + ", " + location.longitude + if (location.hasAccuracy()) " : +- " + location.accuracy + " meters" else "")
        locationManager.removeUpdates(this)
        callback!!.onLocationFound(location)
    }

    override fun onProviderDisabled(provider: String) {
        showLogD("$LOG_TAG Provider disabled : $provider")
        if (method == Method.NETWORK_THEN_GPS
            && provider.contentEquals(LocationManager.NETWORK_PROVIDER)
        ) {
            // Network provider disabled, try GPS
            showLogD("$LOG_TAG Request updates from GPS provider, network provider disabled.")
            requestUpdates(LocationManager.GPS_PROVIDER)
        } else {
            locationManager.removeUpdates(this)
            callback!!.onLocationNotFound()
        }
    }

    override fun onProviderEnabled(provider: String) {
        showLogD("$LOG_TAG Provider enabled : $provider")
    }

    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
        showLogD("$LOG_TAG Provided status changed : $provider : status : $status")
    }

    enum class Method {
        NETWORK, GPS, NETWORK_THEN_GPS
    }

    interface Listener {
        fun onLocationFound(location: Location?)
        fun onLocationNotFound()
    }

}