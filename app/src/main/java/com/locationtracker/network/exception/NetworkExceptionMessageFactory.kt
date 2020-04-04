package com.locationtracker.network.exception

import com.locationtracker.network.exception.NetworkException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface NetworkExceptionMessageFactory {

    fun getErrorMessage(networkException: NetworkException): CharSequence

    fun getErrorMessage(unknownHostException: UnknownHostException): CharSequence

    fun getErrorMessage(socketTimeoutException: SocketTimeoutException): CharSequence

    fun getErrorMessage(connectException: ConnectException): CharSequence
}