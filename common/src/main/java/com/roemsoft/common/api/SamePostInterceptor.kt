package com.roemsoft.common.api

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import timber.log.Timber
import java.io.IOException
import java.util.WeakHashMap

/**
 * 防止短时间内post重复提交
 */
class SamePostInterceptor : Interceptor {

    /*companion object {
        val calls: WeakHashMap<String, WeakReference<Call>> = WeakHashMap()
    }*/
    private val callTimes: WeakHashMap<String, Long> = WeakHashMap()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val method = request.method

        if (method != "POST") {
            return chain.proceed(request)
        }

        val key = bodyToString(request.body)

        val time = System.currentTimeMillis()
        try {
            if (callTimes.contains(key)) {
                val callTime = callTimes[key]
                if (callTime != null && time - callTime <= 500) {
                    chain.call().cancel()
                    throw IOException("same post cancel! $callTime")
                }
            }

            callTimes[key] = time
            return chain.proceed(request)
        } catch (e: IOException) {
            throw e
        } finally {
            if (callTimes.containsKey(key) && callTimes.containsValue(time)) {
                callTimes.remove(key)
            }
        }
    }

    /*private fun check(chain: Interceptor.Chain, request: Request, key: String): Response {
        val needWait = needWait(key)

        if (needWait) {
            Thread.sleep(500)
            return check(chain, request, key)
        } else {

        }
    }*/

    /*private fun needWait(key: String): Boolean {
        if (calls.contains(key)) {
            val callWeakReference = calls[key]
            if (callWeakReference == null) {
                Timber.tag("SamePost").i("不需要等待,直接发请求 call WeakReference not exist:")
                return false
            }
            val call = callWeakReference.get()
            if (call == null || call.isCanceled()) {
                Timber.tag("SamePost").i("不需要等待,直接发请求 call not exist or is canceled: $call")
                return false
            }
            Timber.tag("SamePost").i("请求可能正在等待或正在执行: $call")
            return true
        }
        Timber.tag("SamePost").i("不需要等,直接执行请求")
        return false
    }*/

    private fun bodyToString(request: RequestBody?): String {
        request?.let {
            try {
                val buffer = Buffer()
                request.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                Timber.e(e, "Did not work.")
            }
        }
        return ""
    }
}