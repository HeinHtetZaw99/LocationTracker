package com.locationtracker.network.exception

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkExceptionInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val response = chain.proceed(chain.request())
        when (response.isSuccessful) {
            true  -> return response
            false -> {
                throw NetworkException(response.body , response.code)
            }
        }

    }
}