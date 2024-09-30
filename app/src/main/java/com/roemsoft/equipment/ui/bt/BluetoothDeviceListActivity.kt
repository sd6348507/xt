package com.roemsoft.equipment.ui.bt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.dothantech.printer.IDzPrinter
import com.roemsoft.common.dialog.WaitDialog
import com.roemsoft.common.eventObserve
import com.roemsoft.common.unit.PermissionUtils
import com.roemsoft.common.widget.CustomerToast
import com.roemsoft.common.widget.SpaceItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityBluetoothDeviceListBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.util.BluetoothStateEvent
import com.roemsoft.equipment.util.LPAPIManager
import timber.log.Timber

class BluetoothDeviceListActivity : DataBindingAppCompatActivity() {

    companion object {
        const val REQUEST_ENABLE_BT = 2
        const val REQUEST_DISABLE_BT = 3
        const val EXTRA_DEVICE_ADDRESS ="address"
    }

    private val binding: ActivityBluetoothDeviceListBinding by binding(R.layout.activity_bluetooth_device_list)

    override val viewModel: BluetoothDeviceViewModel by viewModels()

    private var mBluetoothAdapter: BluetoothAdapter? = null

    private val mWaitDialog: WaitDialog by lazy { WaitDialog(this).build() }

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.label_connect_printer)
    }

    @SuppressLint("MissingPermission")
    override fun initView() {
        binding.list.apply {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(SpaceItemDecoration(context))
            adapter = viewModel.adapter
        }

        binding.statusSwitch.setOnClickListener {
            if (binding.statusSwitch.isChecked) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_ENABLE_BT)
            } else {
                val intent = Intent("android.bluetooth.adapter.action.REQUEST_DISABLE")
                startActivityForResult(intent, REQUEST_DISABLE_BT)
            }
            viewModel.updateConnInfo(LPAPIManager.isPrinterConnected())
        }

        requestBTPermission()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun start() {
        // 注册查找蓝牙设备广播
        val findFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(mFindBlueToothReceiver, findFilter)

        // 注册蓝牙开关广播
        val stateFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        registerReceiver(mStateReceiver, stateFilter)

        // 注册连接蓝牙设备广播
    //    val connFilter = IntentFilter(DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE) // 查询打印机缓冲区状态广播，用于一票一控
   //     connFilter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE) // 与打印机连接状态

        initBluetooth()
    }

    override fun onStart() {
        super.onStart()
        viewModel.isCheck.postValue(mBluetoothAdapter?.isEnabled ?: false)

        viewModel.updateConnInfo(LPAPIManager.isPrinterConnected())
    }

    @SuppressLint("MissingPermission")
    override fun setupEvent() {
        super.setupEvent()

        eventObserve(BluetoothStateEvent::class.java) { event ->
            when (event.state) {
                IDzPrinter.PrinterState.Disconnected -> {
                    CustomerToast(this, "打印机断开连接").show()
                    mWaitDialog.hide()
                    viewModel.disconnect()
                }
                IDzPrinter.PrinterState.Connecting -> {
                    CustomerToast(this, "正在连接打印机").show()
                    mWaitDialog.show()
                }
                IDzPrinter.PrinterState.Connected, IDzPrinter.PrinterState.Connected2 -> {
                    mWaitDialog.hide()
                    CustomerToast(this, "连接成功！").show()
                    mWaitDialog.hide()
                    LPAPIManager.getConnDeviceInfo()?.let { viewModel.connectSuccess(it) }
                }
                else -> {
                    mWaitDialog.hide()
                }
            }
        }

        viewModel.isDiscovery.observe(this) {
            if (it) {
                Timber.tag("Bluetooth").i("=========> start discovery")
                startDiscovery()
            } else {
                Timber.tag("Bluetooth").i("=========> cancel discovery")
                cancelDiscovery()
            }
        }

        viewModel.openDevice.observe(this) { device ->
            mBluetoothAdapter?.cancelDiscovery()

            if (!LPAPIManager.isDeviceSupported(device)) {
                CustomerToast(this, "不支持的打印机").showBottom()
                return@observe
            }

            if (!LPAPIManager.openPrinter(device)) {
                CustomerToast(this, "连接打印机失败").showBottom()
                return@observe
            }
        }

        viewModel.openAddress.observe(this) { address ->
            if (!LPAPIManager.openPrinterByAddress(address)) {
                CustomerToast(this, "连接打印机失败").showBottom()
                return@observe
            }
        }
    }

    private fun initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            CustomerToast(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.isCheck.postValue(mBluetoothAdapter?.isEnabled ?: false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                viewModel.isCheck.value = true
                viewModel.isDiscovery.value = true
            }
            if (resultCode == RESULT_CANCELED) {
                viewModel.isCheck.value = false
                viewModel.isDiscovery.value = false
            }
        }
        if (requestCode == REQUEST_DISABLE_BT) {
            viewModel.isCheck.value = false
            viewModel.isDiscovery.value = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        mBluetoothAdapter?.cancelDiscovery()
        mBluetoothAdapter = null

        mFindBlueToothReceiver?.let {
            unregisterReceiver(it)
        }
        mFindBlueToothReceiver = null
        mStateReceiver?.let {
            unregisterReceiver(it)
        }
        mStateReceiver = null

        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_DEVICE_ADDRESS, LPAPIManager.getConnDeviceInfo()?.deviceAddress) })
    }

    private var mFindBlueToothReceiver: BroadcastReceiver? = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { viewModel.addDevice(it) }
                }
            }
        }

    }

    private var mStateReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                    BluetoothAdapter.STATE_ON -> viewModel.isCheck.value = true
                    BluetoothAdapter.STATE_OFF -> viewModel.isCheck.value = false
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        mBluetoothAdapter?.let {
            if (it.isDiscovering) {
                it.cancelDiscovery()
            } else {
                viewModel.clearDevices()

                it.startDiscovery()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun cancelDiscovery() {
        mBluetoothAdapter?.cancelDiscovery()
    }

    private fun requestBTPermission() {
        if (Build.VERSION.SDK_INT < 31) {
            PermissionUtils.checkAndRequestPermissions(this, 10001,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            PermissionUtils.checkAndRequestPermissions(this, 10001,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}