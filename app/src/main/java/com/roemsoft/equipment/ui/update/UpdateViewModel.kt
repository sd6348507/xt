package com.roemsoft.equipment.ui.update

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.BaseApp
import com.roemsoft.common.api.HttpConfig
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.common.isNetworkConnected
import com.roemsoft.download.DownloadInfo
import com.roemsoft.download.DownloadManager
import com.roemsoft.download.IDownloadListener
import com.roemsoft.download.IWorker
import com.roemsoft.equipment.BuildConfig
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*

class UpdateViewModel : BaseViewModel() {

    val appVersion = MutableLiveData(BuildConfig.VERSION_NAME)
    val progress = MutableLiveData<Int>()
    val installVisibility = MutableLiveData(View.GONE)

    private val _lastVersion = MutableLiveData("正在检查是否有新版本")
    val lastVersion: LiveData<String> = _lastVersion

    private val _showUpdateAlert = MutableLiveData<Boolean>()
    val showUpdateDialog: LiveData<Boolean> = _showUpdateAlert

    private var versionCode: Int = -1
    private var downloadManager: DownloadManager? = null
    private var apkPath: String? = null

    fun checkVersion() {
        if (!BaseApp.context.isNetworkConnected()) {
            showToast("网络无法链接")
            return
        }

        viewModelScope.launch {
            repository.checkVersion()
                .collectLatest { result ->
                    result.doSuccess {
                        versionCode = it.code
                        _lastVersion.value = it.name
                        _showUpdateAlert.value = versionCode > BuildConfig.VERSION_CODE
                    }
                    result.doError { _lastVersion.value = it }
                }
        }
    }

    fun requestDownloadApk() {
        if (downloadManager == null) downloadManager = initDownloadManager()
        downloadManager?.downloadApk()
    }

    private fun initDownloadManager(): DownloadManager {
        return DownloadManager(BaseApp.context, worker).apply {
            setDownloadInfo(worker.generateDownloadInfo(versionCode))
        }
    }

    private val worker = object : IWorker {
        override fun generateDownloadInfo(netVersion: Int): DownloadInfo {
            return DownloadInfo(
                saveName = "_temp@ssd-wj.apk",
                url = HttpConfig.DOWNLOAD_URL,
                appVersion = BuildConfig.VERSION_CODE,
                netVersion = netVersion
            )
        }

        override fun preStartDownload() {
            progress.value = 0
        }

        override fun downloadApk(info: DownloadInfo) {
            viewModelScope.launch {
                repository.download()
                    .collectLatest { result ->
                        result.doSuccess {
                            writeCacheFile(it, File(info.savePath), info, listener)
                        }
                        result.doError { showToast(it) }
                    }
            }
        }

        override fun cancelDownload() {}
    }

    private val listener = object : IDownloadListener {
        override fun onRequest() {}

        override fun onResponseError(msg: String) {
            showToast(msg)
        }

        override fun onStart() {
            showToast("开始更新")
        }

        override fun onProgress(progress: Int) {
            this@UpdateViewModel.progress.postValue(progress)
        }

        override fun onFinish(localPath: String) {
            postToast("下载完成")
            installVisibility.postValue(View.VISIBLE)
            apkPath = localPath
        }

        override fun onFailure(message: String) {
            showToast(message)
        }
    }

    fun installApkFile() {
        apkPath ?: return
        downloadManager?.installAPKFile(apkPath!!)
    }

    private fun writeCacheFile(
        body: ResponseBody,
        file: File,
        info: DownloadInfo,
        listener: IDownloadListener
    ) {
        var os: OutputStream? = null

        viewModelScope.launch(Dispatchers.Main + CoroutineExceptionHandler { _, e ->
            // Handler不会处理取消携程产生的异常CancellationException
            when (e) {
                is FileNotFoundException -> {
                    Timber.e("[Download] file not found exception${e.message}")
                    listener.onFailure("未找到文件！")
                }
                is IOException -> {
                    Timber.e("[Download] io exception${e.message}")
                    listener.onFailure("文件输入异常！")
                }
                else -> {
                    e.printStackTrace()
                    listener.onFailure("未知异常！")
                }
            }
        }) {
            listener.onStart()

            Timber.i("[Download] 写入文件开始，文件路径：${file.path}====")
            info.contentLength = body.contentLength()
            Timber.i("[Download] 下载文件长度:${info.contentLength}====")

            try {
                withContext(Dispatchers.IO) {
                    os = FileOutputStream(file)
                    var readLength = 0L
                    val buff = ByteArray(1024 * 10)
                    var len = 0

                    while (body.byteStream().read(buff).also { len = it } != -1) {
                        os?.write(buff, 0, len)

                        readLength += len
                        info.readLength = readLength
                        val percent = (info.readLength * 100 / info.contentLength).toInt()
                        Timber.i("[Download] 当前已写入长度:${info.readLength}")

                        listener.onProgress(percent)
                        if (percent == 100) {
                            listener.onFinish(file.path)
                        }
                    }
                }
            } finally {
                withContext(Dispatchers.IO) {
                    os?.flush()
                    os?.close()
                    body.byteStream().close()
                    Timber.i("[Download] 写入文件结束，文件长度:${file.length()}")
                }
            }
        }
    }
}