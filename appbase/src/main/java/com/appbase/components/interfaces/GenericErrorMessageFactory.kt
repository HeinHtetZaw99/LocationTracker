package com.appbase.components.interfaces

import androidx.annotation.StringRes
import com.appbase.models.vos.ReturnResult


interface GenericErrorMessageFactory {
    fun getErrorMessage(throwable: Throwable): CharSequence

    fun getErrorMessage(throwable: Throwable, @StringRes defaultText: Int): CharSequence

    fun getError(throwable: Throwable): ReturnResult

    fun getError(throwable: Throwable, @StringRes defaultText: Int): ReturnResult
}