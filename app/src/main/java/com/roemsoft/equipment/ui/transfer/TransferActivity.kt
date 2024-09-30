package com.roemsoft.equipment.ui.transfer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.dothantech.printer.IDzPrinter
import com.roemsoft.common.dialog.ProgressBarDialog
import com.roemsoft.common.dialog.WaitDialog
import com.roemsoft.common.eventObserve
import com.roemsoft.common.hideSoftKeyboard
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.CustomerToast
import com.roemsoft.equipment.App
import com.roemsoft.equipment.R
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.databinding.ActivityTransferBinding
import com.roemsoft.equipment.databinding.LayoutPrintBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.ScanActivity
import com.roemsoft.equipment.ui.bt.BluetoothDeviceListActivity
import com.roemsoft.equipment.ui.search.DepartmentSearchActivity
import com.roemsoft.equipment.ui.search.PersonSearchActivity
import com.roemsoft.equipment.util.BluetoothProgressEvent
import com.roemsoft.equipment.util.BluetoothStateEvent
import com.roemsoft.equipment.util.LPAPIManager
import com.roemsoft.zltd.ScannerUnitBroadcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

class TransferActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityTransferBinding by binding(R.layout.activity_transfer)

    override val viewModel: TransferViewModel by viewModels()

    private val dialog by lazy {
        ProgressBarDialog(this).build()
    }

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val printBinding: LayoutPrintBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.layout_print, null, false)
    }
    private var stateDialog: WaitDialog?= null
    // 是否需要更新打印预览
    private var needUpdatePreview: Boolean = false
    // 打印预览图片
    private var preview: Bitmap? = null

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun initView() {
        if (App.isPda()) {
            binding.scanImg.visibility = View.INVISIBLE

            val scannerUnit = ScannerUnitBroadcast(this).apply {
                scannerResult.observe(this@TransferActivity) { onScannerResult(it) }
            }

            lifecycle.addObserver(scannerUnit)
        }

        binding.searchBtn.onSingleClick {
            viewModel.assetsNo.value = binding.searchEt.text.trim().toString()
            viewModel.loadData()
        }

        binding.scanImg.onSingleClick {
            launcher.launch(Intent(this, ScanActivity::class.java))
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ScanActivity.SCAN_SUCCESS -> {
                    val code = result.data?.getStringExtra(ScanActivity.SCAN_RESULT) ?: ""
                    viewModel.assetsNo.value = code
                    viewModel.loadData()
                }
                DepartmentSearchActivity.ACTION_SEARCH_DEPARTMENT -> {
                    viewModel.department.value = result.data?.getStringExtra(DepartmentSearchActivity.RESULT_SELECTED)
                }
                PersonSearchActivity.ACTION_SEARCH_PERSON -> {
                    viewModel.person.value = result.data?.getStringExtra(PersonSearchActivity.RESULT_SELECTED)
                }
            }
        }

        binding.departmentNew.onSingleClick {
            launcher.launch(Intent(this, DepartmentSearchActivity::class.java))
        }

        binding.personNew.onSingleClick {
            launcher.launch(Intent(this, PersonSearchActivity::class.java))
        }

        binding.clear.onSingleClick {
            preview = null
            viewModel.clear()
        }

        binding.submitBtn.onSingleClick {
            viewModel.submit()
        }

        binding.printBtn.onSingleClick {
            if (preview == null || needUpdatePreview) {
                generatePreviewBitmap()
                needUpdatePreview = false
            }
            if (preview == null) {
                CustomerToast(this, "生成预览图片错误", Toast.LENGTH_SHORT).show()
            } else {
                printBitmap(preview!!)
            }
        }

        binding.toggle.onSingleClick {
            if (viewModel.isConnected.value!!) {
                if (LPAPIManager.isPrinting()) {
                    CustomerToast(this, "等待打印完成后再断开连接").showBottom()
                } else {
                    LPAPIManager.closePrinter()
                }
            } else {
                startActivity(Intent(this, BluetoothDeviceListActivity::class.java))
            }
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.setText(R.string.label_transfer)
    }

    @SuppressLint("MissingPermission")
    override fun setupEvent() {
        super.setupEvent()

        viewModel.loading.observe(this) {
            if (it) {
                dialog.show()
            } else {
                dialog.hide()
            }
        }

        viewModel.needUpdatePreview.observe(this) {
            needUpdatePreview = it
        }

        eventObserve(BluetoothStateEvent::class.java) { _ ->
            updateConnState()
        }

        eventObserve(BluetoothProgressEvent::class.java) { event ->
            if (event.progress == IDzPrinter.PrintProgress.Success) {
                onPrintSuccess()
                return@eventObserve
            }
            if (event.progress == IDzPrinter.PrintProgress.Failed) {
                onPrintFailed()
                return@eventObserve
            }
        }
    }

    private fun onScannerResult(code: String) {
        viewModel.assetsNo.value = code.trim()
        viewModel.loadData()
    }

    private fun printBitmap(bitmap: Bitmap) {
        if (!LPAPIManager.isPrinterConnected()) {
            CustomerToast(this, "未连接打印机", Toast.LENGTH_SHORT).show()
            return
        }
        if (LPAPIManager.printBitmap(bitmap)) {
            onPrintStart()
        } else {
            onPrintFailed()
        }
    }

    private fun generatePreviewBitmap() {
        val printData = viewModel.getSelectData()
        if (printData != null) {
            val bitmap = createPrintContent(printData)
            preview = LPAPIManager.drawBitmapAndQrCode(bitmap, printData.no)
        }
    }

    private fun createPrintContent(data: PrinterData): Bitmap {
        printBinding.data = data
        printBinding.executePendingBindings()
        val view = printBinding.root
        val me = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(me, me)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        return generateBitmapWithView(view)
    }

    private fun generateBitmapWithView(view: View, degrees: Float = 0f): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val drawable = view.background
        if (drawable != null) {
            drawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        val matrix = Matrix()
        matrix.setRotate(degrees, bitmap.width / 2f, bitmap.height / 2f)
        val printBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return printBitmap
    }

    private fun updateConnState() {
        viewModel.isConnected.value = LPAPIManager.isPrinterConnected()
    }

    private fun onPrintStart() {
        val title = "正在打印标签..."
        showStateDialog(title)
    }

    private fun onPrintFailed() {
        clearStateDialog()
        CustomerToast(this, "标签打印失败", Toast.LENGTH_SHORT).show()
    }

    private fun onPrintSuccess() {
        clearStateDialog()
        CustomerToast(this, "标签打印成功", Toast.LENGTH_SHORT).show()
    }

    private fun showStateDialog(title: String) {
        if (stateDialog != null && stateDialog!!.isShowing()) {
            stateDialog!!.setTitle(title)
        } else {
            stateDialog = WaitDialog(this).build().apply { show(title) }
        }
    }

    private fun clearStateDialog() {
        if (stateDialog != null && stateDialog!!.isShowing()) {
            stateDialog!!.hide()
        }
        stateDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        LPAPIManager.release()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //如果是点击事件，获取点击的view，并判断是否要收起键盘
        if (ev.action == MotionEvent.ACTION_DOWN) {
            //获取目前得到焦点的view
            val v = currentFocus
            //判断是否要收起并进行处理
            if (v != null && isShouldHideKeyboard(v, ev)) {
                this.hideSoftKeyboard(v.windowToken)
                v.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //判断是否要收起键盘
    private fun isShouldHideKeyboard(v: View, event: MotionEvent): Boolean {
        //如果目前得到焦点的这个view是editText的话进行判断点击的位置
        if (v is EditText) {
            val point = intArrayOf(0, 0)
            v.getLocationInWindow(point)
            val left = point[0]
            val top = point[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            // 点击EditText的事件，忽略它。
            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上
        return false
    }
}