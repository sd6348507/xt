package com.roemsoft.download

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import java.io.File
import java.util.ArrayList

class DownloadManager(var context: Context, private var mWorker: IWorker) : LifecycleObserver {

    companion object {
        private val DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    }

    // 当前下载列表
    private val mDownloadApkList: ArrayList<DownloadInfo> = ArrayList()

    private var mDownloadInfo: DownloadInfo? = null

    fun setDownloadInfo(info: DownloadInfo) {
        mDownloadInfo = info
    }

    private fun checkApk(): Boolean {
        mDownloadInfo?.let {
            return File(DOWNLOAD_DIR, it.saveName).exists()
        } ?: return false
    }

    private fun deleteApk() {
        mDownloadInfo?.let {
            val file = File(DOWNLOAD_DIR, it.saveName)
            if (file.exists()) {
                file.delete()
            }
        }

    }

    // 下载前的准备工作
    private fun prepareDownload(): Boolean {
        // 检查是否在下载列表中
        mDownloadInfo?.let {
            mDownloadApkList.forEach { item ->
                if (item.savePath == it.savePath) {
                    Toast.makeText(context, "当前文件已经在下载列表中", Toast.LENGTH_SHORT).show()
                    return false
                }
            }

            val file = File(DOWNLOAD_DIR, it.saveName)
            it.savePath = file.path
            if (file.exists()) {
                file.delete()
            }
            return true
        } ?: return false
    }

    private fun preStartDownload() {
        mDownloadInfo?.let {
            mWorker.preStartDownload()
        }
    }

    // 开始下载
    private fun startDownload() {
        mDownloadInfo?.let {
            mWorker.downloadApk(it)
        }
    }

    fun downloadApk() {
        mDownloadInfo?.let {
            if (it.netVersion > it.appVersion) {
                Log.i("DownloadManager", "==== app need update ====")
                if (prepareDownload()) {
                    preStartDownload()
                    startDownload()
                }
            } else {
                Toast.makeText(context, "当前是最新版本", Toast.LENGTH_SHORT).show()
                // 删除本地已有的安装包
                if (checkApk()) {
                    deleteApk()
                }
            }
        }
    }

    /**
     * 安装apk
     * 通过广播{@link PackageReceiver}来监听安装完成再删除apk文件
     *
     */
    fun installAPKFile(apkPath: String): Boolean {
        val apkFile = File(apkPath)
        Log.d("DownloadManager", "[Install] install apk file:$apkPath")
        if (!apkFile.exists()) {
            Toast.makeText(context, "App安装文件不存在!", Toast.LENGTH_SHORT).show()
            return false
        }

        // 在24及其以上版本，解决崩溃异常：
        // android.os.FileUriExposedException: file:///storage/emulated/0/xxx exposed beyond app through Intent.getData()
        val apkFileUri: Uri = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            true -> FileProvider.getUriForFile(context, "com.roemsoft.download.xt", apkFile)  // ${BuildConfig.LIBRARY_PACKAGE_NAME}
            false -> Uri.fromFile(apkFile)
        }

        val installIntent = Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            action = Intent.ACTION_VIEW

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
            setDataAndType(apkFileUri, "application/vnd.android.package-archive")
        }
        try {
            context.startActivity(installIntent)
        } catch (e: ActivityNotFoundException) {
            Log.d("DownloadManager", "[Install] installAPKFile exception:$e")
            return false
        }
        return true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mWorker.cancelDownload()
    }
}