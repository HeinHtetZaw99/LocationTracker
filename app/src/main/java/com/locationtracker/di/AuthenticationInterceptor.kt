package com.locationtracker.di

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor() : Interceptor {
    private var bearerToken : String? = ""
    private var token : String? = ""
    private var customToken : String? = null
    private var customTag : String? = null
    private var onlyUseCustomHeader : Boolean = false

    private var customName : String? = null


    fun setCustom(customName : String? , customTag : String? , customToken : String?) {
        this.customToken = customToken
        this.customTag = customTag
        this.customName = customName
    }


    fun setOnlyUseCustomHeader(onlyUseCustomHeader : Boolean) {
        this.onlyUseCustomHeader = onlyUseCustomHeader
    }


    constructor(
        bearerToken : String?,
        token : String?
    ) : this() {
        this.bearerToken = bearerToken
        this.token = token
    }

    @Throws(IOException::class)
    override fun intercept(chain : Interceptor.Chain) : Response {
        val builder = chain.request().newBuilder()
        if (!onlyUseCustomHeader) {
            if (customName != null && customToken != null) {
                if (customTag == null)
                    builder.addHeader("$customName" , "$customToken")
                else
                    builder.addHeader("$customName" , "$customTag $customToken")
            }

            if (bearerToken != null && bearerToken != "")
                builder.addHeader("Authorization" , "Bearer $bearerToken")
            if (token != null && token != "")
                builder.addHeader("token" , "$token")
        } else {
            if (customName != null && customToken != null) {
                if (customTag == null)
                    builder.addHeader("$customName" , "$customToken")
                else
                    builder.addHeader("$customName" , "$customTag $customToken")
            }
        }
        builder.addHeader("Accept" , "application/json")

        val request = builder.build()
        return chain.proceed(request)
    }

}
