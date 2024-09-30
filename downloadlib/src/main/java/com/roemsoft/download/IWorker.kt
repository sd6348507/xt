package com.roemsoft.download

interface IWorker {

    fun generateDownloadInfo(netVersion: Int): DownloadInfo

    fun preStartDownload()

    fun downloadApk(info: DownloadInfo)

    fun cancelDownload()
}