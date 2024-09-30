package com.roemsoft.equipment.util

import android.bluetooth.BluetoothDevice
import android.graphics.Bitmap
import android.widget.Toast
import com.dothantech.lpapi.LPAPI
import com.dothantech.printer.IDzPrinter
import com.dothantech.printer.IDzPrinter.PrintFailReason
import com.dothantech.printer.IDzPrinter.PrinterAddress
import com.dothantech.printer.IDzPrinter.PrinterInfo
import com.dothantech.printer.IDzPrinter.PrinterState
import com.roemsoft.common.postEvent
import timber.log.Timber

/**
 * LPAPI 打印机
 */
object LPAPIManager {

    // LPAPI 打印机操作相关的回调函数
    private val mCallback: LPAPI.Callback = object : LPAPI.Callback {

        // 打印机连接状态发生变化时被调用
        override fun onStateChange(address: PrinterAddress, state: PrinterState) {
            when (state) {
                PrinterState.Connecting -> Timber.tag("LPAPI").i("LPAPI[ 正在连接打印机 ]")
                PrinterState.Connected, PrinterState.Connected2 -> Timber.tag("LPAPI").i("LPAPI[ 打印机连接成功 ]")
                PrinterState.Printing -> Timber.tag("LPAPI").i("LPAPI[ 打印机正在打印 ]")
                PrinterState.Disconnected -> Timber.tag("LPAPI").i("LPAPI[ 打印机断开连接 ]")
                else -> Timber.tag("LPAPI").i("LPAPI[ 除打印之外的其他工作 ]")
            }
            postEvent(BluetoothStateEvent(state))
        }

        // 蓝牙适配器状态发生变化时被调用
        override fun onProgressInfo(p0: IDzPrinter.ProgressInfo?, p1: Any?) { }

        // 打印进度过程发生变化时的回调函数
        override fun onPrintProgress(
            address: PrinterAddress,
            bitmapData: IDzPrinter.PrintData,
            progress: IDzPrinter.PrintProgress,
            addiInfo: Any
        ) {
            when (progress) {
                IDzPrinter.PrintProgress.Success -> Timber.tag("LPAPI").i("LPAPI[ 标签打印成功，打印页面数${addiInfo as Int} ]")
                IDzPrinter.PrintProgress.Failed -> Timber.tag("LPAPI").i("LPAPI[ 标签打印失败， 原因：${(addiInfo as PrintFailReason)} ]")
                else -> {}
            }
            postEvent(BluetoothProgressEvent(progress, addiInfo))
        }

        // 搜索到打印机或者通过 NFC 扫描到打印机时的回调函数
        override fun onPrinterDiscovery(p0: PrinterAddress?, p1: Any?) { }

    }

    private val api: LPAPI = LPAPI.Factory.createInstance(mCallback)

    /**
     * 异步方式打开指定的蓝牙设备
     */
    fun openPrinter(device: BluetoothDevice): Boolean {
        return isDeviceSupported(device) && api.openPrinter(device)
    }

    fun openPrinterByAddress(address: PrinterAddress): Boolean {
        return api.openPrinterByAddress(address)
    }

    fun closePrinter() {
        Timber.tag("LPAPI").i("LPAPI[ 断开连接，${isPrinting()} ]")
        if (!isPrinting()) {
            api.closePrinter()
        }
    }

    fun isDeviceSupported(device: BluetoothDevice): Boolean {
        return api.isDeviceSupported(device, null)
    }

    /**
     * 获取已连接打印机详细信息
     */
    fun getConnDeviceInfo(): PrinterInfo? {
        return if (isPrinterConnected()) {
            api.printerInfo
        } else {
            null
        }
    }


    // 判断当前打印机是否连接
    fun isPrinterConnected(): Boolean {
        // 调用LPAPI对象的getPrinterState方法获取当前打印机的连接状态
        val state = api.printerState

        // 打印机未连接
        if (state == null || state == PrinterState.Disconnected) {
            return false
        }

        return state != PrinterState.Connecting
    }

    fun isPrinting(): Boolean {
        val state = api.printerState
        return state != null && state == PrinterState.Printing
    }

    fun release() {
        api.closePrinter()
    //    api.quit()
    }

    fun drawBitmap(bitmap: Bitmap, x: Float, y: Float, width: Float, height: Float): Bitmap {
        // 开始绘图任务，传入参数(页面宽度, 页面高度), 顺时针旋转角度，0/90/180/270，默认值 0
        api.startJob(70.0, 40.0, 0)

        // 绘制位图对象
        api.drawBitmap(bitmap, 0.0, 0.0, 70.0, 40.0)

        // 结束绘图任务
        api.endJob()

        return api.jobPages[0]
    }

    fun drawBitmapAndQrCode(bitmap: Bitmap, text: String): Bitmap {
        // 开始绘图任务，传入参数(页面宽度, 页面高度), 顺时针旋转角度，0/90/180/270，默认值 0
        api.startJob(70.0, 40.0, 90)
        // api.startJob(60.0, 40.0, 90)  // 40*60 原本是40*70的纸张

        // 绘制位图对象
        api.drawBitmap(bitmap, 0.0, 2.0, 68.0, 36.0)
        // api.drawBitmap(bitmap, 0.0, 2.0, 58.0, 36.0)

        // 绘制 QRCode 二维码
        api.draw2DQRCode(text, 49.5, 2.0, 18.0)
        // api.draw2DQRCode(text, 39.5, 2.0, 18.0)

        // 结束绘图任务
        api.endJob()

        return api.jobPages[0]
    }

    fun printBitmap(bitmap: Bitmap): Boolean {
        return api.printBitmap(bitmap, null)
    }
}