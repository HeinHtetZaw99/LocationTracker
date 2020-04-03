package com.appbase.components.impls

import android.content.Context
import com.apbase.R
import com.appbase.components.exception.NetworkException
import com.appbase.components.interfaces.NetworkExceptionMessageFactory
import com.appbase.showLogE
import okhttp3.ResponseBody
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class NetworkExceptionMessageFactoryImpl @Inject constructor(
    private val context: Context
) : NetworkExceptionMessageFactory {

    override fun getErrorMessage(networkException: NetworkException): CharSequence {
        return when (networkException.errorCode) {
            400 -> parseMessageFromErrorBody(networkException.errorBody)
            401 -> parseMessageFromErrorBody(networkException.errorBody)
            403 -> parseMessageFromErrorBody(networkException.errorBody)
            404 -> context.getString(R.string.error_server_404)
            408 -> parseMessageFromErrorBody(networkException.errorBody)
            409 -> parseMessageFromErrorBody(networkException.errorBody)
            422 -> parseMessageFromErrorBody(networkException.errorBody)
            500 -> context.getString(R.string.error_server_500)
            else -> context.getString(R.string.error_generic)
        }
    }

    private fun parseMessageFromErrorBody(errorBody: ResponseBody?): CharSequence {

        if (errorBody == null) {
            return context.getString(R.string.error_generic)
        }

        val errorBodyInString =
            try {
                errorBody.string()
            } catch (e: Exception) {
                this.javaClass.showLogE("ce occurred at parsing error body string ")
                this.javaClass.showLogE(e)
                ""
            }

        this.javaClass.showLogE("error body in string : $errorBodyInString")

//        try {
//            val errorBodyJson = JSONObject(errorBodyInString)
//            return errorBodyJson.getString("message")
//        } catch (exception: Exception) {
//            this.javaClass.showLogE("Error in parsing error message body ${exception.message}")
//        }

        return errorBodyInString
    }

    override fun getErrorMessage(unknownHostException: UnknownHostException): CharSequence {
        return context.getString(R.string.error_no_internet)
    }

    override fun getErrorMessage(socketTimeoutException: SocketTimeoutException): CharSequence {
        return context.getString(R.string.error_socket_timeout)
    }

    override fun getErrorMessage(connectException: ConnectException): CharSequence {
        return context.getString(R.string.error_no_internet)
    }


}