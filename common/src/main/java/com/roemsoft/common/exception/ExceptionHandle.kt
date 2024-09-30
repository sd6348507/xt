package com.roemsoft.common.exception

import com.squareup.moshi.JsonDataException
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

class ExceptionHandle {
    companion object {
        var errorMsg = "请求失败，请稍后重试"

        fun handleException(e: Throwable): String {
            e.printStackTrace()
            if (e is SocketTimeoutException
                    || e is ConnectException
                    || e is HttpException
            ) { //均视为网络错误
                Timber.tag("Request_Exception").e("====> 网络连接异常: ${e.message} <====")
                errorMsg = "网络连接异常"
            } else if (e is JsonDataException
                    || e is JSONException
                    || e is ParseException
            ) {   //均视为解析错误
                Timber.tag("Request_Exception").e("====> 数据解析异常: ${e.message} <====")
                errorMsg = "数据解析异常"
            } else if (e is ApiException) {//服务器返回的错误信息
                errorMsg = e.message.toString()
            } else if (e is UnknownHostException) {
                Timber.tag("Request_Exception").e("====> 网络连接异常: ${e.message} <====")
                errorMsg = "网络连接异常"
            } else if (e is IllegalArgumentException) {
                errorMsg = "参数错误"
            } else {//未知错误
                try {
                    Timber.tag("Request_Exception").e("====> 错误: ${e.message} <====")
                } catch (e1: Exception) {
                    Timber.tag("Request_Exception").e("====> 未知错误Debug调试: ${e.message} <====")
                }
                errorMsg = "未知错误，可能抛锚了吧~"
            }
            return errorMsg
        }

    }
}