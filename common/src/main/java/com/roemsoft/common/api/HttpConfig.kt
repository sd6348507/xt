package com.roemsoft.common.api

open class HttpConfig {

    companion object {

        const val URL_MOBILE = "http://119.29.225.125:53792"          // 外网 /ajax/
        const val URL_WIFI = "http://192.168.111.248:8567"        // 内网 /ajax/
        const val URL_TEST = "http://192.168.1.135:31/ajax/"            // 测试

        const val BASE_URL = "http://119.29.225.125:53792"          // 外网 /ajax/

        const val URL_YUN = "http://120.25.197.64:6655/JX/"     // 云

        const val VERSION_URL = "PDAedition.aspx"

        const val DOWNLOAD_URL = "XT.apk"

        const val DEFAULT_TIME_OUT = 20L  // 单位：秒

        const val REQ_TYPE = "ReqType"
        const val REQ_STR = "ReqStr"
    }

    enum class NetUrl(val title: String, val url: String, val index: Int) {
        MOBILE("外网", URL_MOBILE, 0),
        WIFI("内网", URL_WIFI, 1)
    }

}