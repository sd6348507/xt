package com.roemsoft.common.api

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.io.IOException

class LogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Timber.i("${request.method}->${request.url}")

        request.headers.also { Timber.i("Headers:$it") }

        request.body?.let { Timber.i("RequestBody:${bodyToString(it)}") }

        return chain.proceed(request)
    }

    private fun bodyToString(request: RequestBody?): String? {
        request?.let {
            try {
                val buffer = Buffer()
                request.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                Timber.e(e, "Did not work.")
            }
        }
        return null
    }

}