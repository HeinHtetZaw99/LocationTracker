package com.appbase.components.impls

import android.content.Context
import com.apbase.R
import com.appbase.components.exception.NetworkException
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.components.interfaces.NetworkExceptionMessageFactory
import com.appbase.models.vos.ReturnResult
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class GenericErrorMessageFactoryImpl @Inject constructor(
    private val context: Context,
    private val networkExceptionMessageFactory: NetworkExceptionMessageFactory
) : GenericErrorMessageFactory {
    override fun getErrorMessage(throwable: Throwable): CharSequence {
        return getErrorMessage(throwable, 0)
    }

    override fun getErrorMessage(throwable: Throwable, defaultText: Int): CharSequence {
        return when (throwable) {
            is UnknownHostException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is SocketTimeoutException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is ConnectException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            is NetworkException -> networkExceptionMessageFactory.getErrorMessage(throwable)
            else -> {
                return try {
                    context.getString(defaultText)
                } catch (e: Exception) {
                    throwable.message ?: context.getString(R.string.error_generic)
                }
            }
        }
    }

    override fun getError(throwable: Throwable): ReturnResult {
        return getError(throwable, 0)
    }

    override fun getError(throwable: Throwable, defaultText: Int): ReturnResult {
        return when (throwable) {
            is UnknownHostException -> ReturnResult.NetworkErrorResult
            is SocketTimeoutException -> ReturnResult.NetworkErrorResult
            is ConnectException -> ReturnResult.NetworkErrorResult
            is NetworkException -> getNetworkError(throwable)
            else -> ReturnResult.ErrorResult(getErrorMessage(throwable, defaultText))
        }
    }

    private fun getNetworkError(networkException: NetworkException): ReturnResult {
        return when (networkException.errorCode) {
            301 -> ReturnResult.DialogResult(ReturnResult.DialogType.NewVersion)
            401 -> ReturnResult.DialogResult(ReturnResult.DialogType.SessionExpired)
            402 -> ReturnResult.DialogResult(ReturnResult.DialogType.PaymentOverdue)
            else -> ReturnResult.ErrorResult(getError(networkException))
        }
    }

}