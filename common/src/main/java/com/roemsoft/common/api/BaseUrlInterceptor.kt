package com.roemsoft.common.api

import com.roemsoft.common.BaseApp
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class BaseUrlInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val oldHttpUrl = request.url
        val builder = request.newBuilder()

        val newHttpUrl = BaseApp.baseUrl.toHttpUrl()

        /*if (oldHttpUrl.toUrl().path.contains(HttpConfig.VERSION_URL)) {
            // 更改版本号查询地址
            val version = HttpConfig.URL_YUN.toHttpUrl()
                .newBuilder()
                .addPathSegment(HttpConfig.VERSION_URL)
                .build()
            Timber.tag("url").i("====> new_url_full:${version.toUrl()} <====")
            return chain.proceed(builder.url(version).build())
        }*/

        /*if (oldHttpUrl.toUrl().path.contains(HttpConfig.DOWNLOAD_URL)) {
            // 更改安装包下载地址
            val download = HttpConfig.URL_YUN.toHttpUrl()
                .newBuilder()
                .addPathSegment(HttpConfig.DOWNLOAD_URL)
                .build()
            Timber.tag("url").i("====> new_url_full:${download.toUrl()} <====")
            return chain.proceed(builder.url(download).build())
        }*/

        if (oldHttpUrl.toUrl().host != newHttpUrl.toUrl().host || oldHttpUrl.toUrl().port != newHttpUrl.toUrl().port) {
            val newFullUrl = oldHttpUrl
                .newBuilder()
                // 更换网络协议
                .scheme(newHttpUrl.scheme)
                // 更换主机名
                .host(newHttpUrl.host)
                // 更换端口
                .port(newHttpUrl.port)
                .build()
            Timber.tag("url").i("====> new_url_full:${newFullUrl.toUrl()} <====")
            return chain.proceed(builder.url(newFullUrl).build())
        }

        return chain.proceed(request)
    }

}