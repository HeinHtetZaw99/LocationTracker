package com.appbase.models.vos

sealed class ReturnResult {
    class ErrorResult(var errorMsg: Any) : ReturnResult()
    class PositiveResult(var msg: Any) : ReturnResult()
    class DialogResult(type: DialogType) : ReturnResult()
    object NetworkErrorResult : ReturnResult()

    sealed class DialogType {
        object PaymentOverdue : DialogType()
        object SessionExpired : DialogType()
        object NewVersion : DialogType()
    }
}