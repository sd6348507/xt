package com.roemsoft.common.bean

import com.roemsoft.common.exception.ExceptionHandle
import java.lang.Exception

sealed class RespResult<out R> {
    data class Success<out T>(val data: T) : RespResult<T>()
    data class Failure(val message: String) : RespResult<Nothing>()
    data class Error(val exception: Exception) : RespResult<Nothing>()
}

inline fun <reified T> RespResult<T>.doSuccess(success: (T) -> Unit) {
    if (this is RespResult.Success) {
        success(this.data)
    }
}

inline fun <reified T> RespResult<T>.doFailure(failure: (String) -> Unit) {
    if (this is RespResult.Failure) {
        failure(this.message)
    }
}

inline fun <reified T> RespResult<T>.doError(error: (String) -> Unit) {
    if (this is RespResult.Error) {
        error(ExceptionHandle.handleException(this.exception))
    }
}