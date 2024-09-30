package com.roemsoft.download

data class DownloadInfo(
    var savePath: String = "",      // apk储存位置
    val saveName: String = "",      // apk文件名
    val url: String = "" ,          // apk的下载URL
    val appVersion: Int = -1,    // 当前App版本
    var netVersion: Int = -1,    // 需要更新的版本
    var contentLength: Long = 0L,   // apk总长度
    var readLength: Long = 0L       // 下载长度
)