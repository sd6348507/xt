package com.roemsoft.equipment.ui.bt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dothantech.printer.IDzPrinter
import com.dothantech.printer.IDzPrinter.PrinterAddress
import com.dothantech.printer.IDzPrinter.PrinterInfo
import com.roemsoft.common.preference.Preference
import com.roemsoft.equipment.ui.BaseViewModel

@SuppressLint("MissingPermission")
class BluetoothDeviceViewModel : BaseViewModel() {

    val isCheck = MutableLiveData(false)

    val isDiscovery = MutableLiveData(false)

    val connDevice = MutableLiveData<BluetoothDevice?>(null)

    val connDeviceStr = MutableLiveData<String?>(null)

    private var lastAddress: String by Preference(Preference.BLUETOOTH_MAC, "")

    private var lastName: String by Preference(Preference.BLUETOOTH_NAME, "")

    val isConn = MutableLiveData(false)

    private var _openDevice = MutableLiveData<BluetoothDevice>()
    val openDevice: LiveData<BluetoothDevice> = _openDevice

    private var _openAddress = MutableLiveData<PrinterAddress>()
    val openAddress: LiveData<PrinterAddress> = _openAddress

    val adapter: BluetoothDeviceAdapter by lazy {
        BluetoothDeviceAdapter().apply {
            onItemClickResult { _, _, device ->
                _openDevice.value = device
            }
        }
    }

    fun toggle() {
        isDiscovery.value = !(isDiscovery.value ?: true)
    }

    fun addDevice(device: BluetoothDevice) {
        adapter.addData(device)
    }

    fun clearDevices() {
        adapter.clear()
    }

    fun disconnect() {
    //    isConn.value = false
   //     connDevice.value = lastAddress
        updateConnInfo(false)
    }

    fun connectSuccess(info: PrinterInfo) {
        lastName = info.deviceName
        lastAddress = info.deviceAddress
        updateConnInfo(true)
    }

    fun updateConnInfo(isConnected: Boolean) {
        isConn.value = isConnected

        if (lastName.isEmpty()) {
            connDeviceStr.value = lastAddress
        } else {
            connDeviceStr.value = "$lastName $lastAddress"
        }
    }

    fun connectLastAddress() {
        if (!isConn.value!! && lastAddress.isNotEmpty()) {
            _openAddress.value = coverToPrinterAddress(lastName, lastAddress)
        }
    }

    private fun coverToPrinterAddress(name: String, address: String) = PrinterAddress(name, address, IDzPrinter.AddressType.BLE)
}