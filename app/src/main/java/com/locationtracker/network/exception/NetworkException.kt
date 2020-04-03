package com.network.exception

import okhttp3.ResponseBody
import java.io.IOException

data class NetworkException constructor(
    var errorBody: ResponseBody? = null,
    var errorCode: Int
) : IOException() {

}