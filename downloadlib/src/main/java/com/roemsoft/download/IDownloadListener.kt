package com.roemsoft.download

interface IDownloadListener {

    fun onRequest()

    fun onResponseError(msg: String)

    fun onStart()

    fun onProgress(progress: Int)

    fun onFinish(localPath: String)

    fun onFailure(message: String)
}