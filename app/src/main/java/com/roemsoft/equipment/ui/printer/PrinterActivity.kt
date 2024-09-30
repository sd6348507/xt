package com.roemsoft.equipment.ui.printer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dothantech.printer.IDzPrinter
import com.roemsoft.common.animation_dsl.AnimSet
import com.roemsoft.common.animation_dsl.animSet
import com.roemsoft.common.dialog.BottomContainerDialog
import com.roemsoft.common.dialog.WaitDialog
import com.roemsoft.common.dialog.bottomContainerDialog
import com.roemsoft.common.dpToPx
import com.roemsoft.common.eventObserve
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.CustomerToast
import com.roemsoft.common.widget.MarginItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.databinding.ActivityPrinterBinding
import com.roemsoft.equipment.databinding.DialogPrinterBottomBinding
import com.roemsoft.equipment.databinding.LayoutPrintBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.bt.BluetoothDeviceListActivity
import com.roemsoft.equipment.util.BluetoothProgressEvent
import com.roemsoft.equipment.util.BluetoothStateEvent
import com.roemsoft.equipment.util.LPAPIManager

class PrinterActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityPrinterBinding by binding(R.layout.activity_printer)

    override val viewModel: PrinterViewModel by viewModels()

    private val dialogBinding: DialogPrinterBottomBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.dialog_printer_bottom, null, false)
    }
    private val printBinding: LayoutPrintBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.layout_print, null, false)
    }
    private var stateDialog: WaitDialog?= null
    // 是否需要更新打印预览
    private var needUpdatePreview: Boolean = true
    private var preview: Bitmap? = null


    private lateinit var searchDialog: BottomContainerDialog

    private var bottomAnim: AnimSet? = null
    private var bottomLayoutHeight = 0
    private var isShowBottom = false

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {
        searchDialog = bottomContainerDialog {
            view = {
                dialogBinding.confirm.onSingleClick {
                    viewModel.no.value = dialogBinding.no.text.toString().trim()
                    viewModel.name.value = dialogBinding.name.text.toString().trim()
                    viewModel.department.value = dialogBinding.department.text.toString().trim()
                    viewModel.area.value = dialogBinding.area.text.toString().trim()
                    viewModel.worker.value = dialogBinding.worker.text.toString().trim()
                    viewModel.refreshData()
                    dismiss()
                }
                dialogBinding.cancel.onSingleClick {
                    dismiss()
                }
                dialogBinding.root
            }
        }

        binding.list.apply {
            adapter = viewModel.adapter
            layoutManager = LinearLayoutManager(this@PrinterActivity)
            addItemDecoration(MarginItemDecoration(dpToPx(4)))
        }

        binding.search.onSingleClick {
            if (searchDialog.isShowing()) {
                viewModel.refreshData()
                searchDialog.dismiss()
            } else {
                searchDialog.show()
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

        binding.bottomLayout.measure(0, 0)
        bottomLayoutHeight = binding.bottomLayout.measuredHeight

        binding.bottomLayout.visibility = if (isShowBottom) View.VISIBLE else View.GONE
        binding.cancelPrint.onSingleClick {
            changeModel(false)
        }

        createAnimation()

        binding.previewBtn.onSingleClick {
            if (viewModel.getSelectData() == null) {
                CustomerToast(this, "未选择需要打印的数据", Toast.LENGTH_SHORT).show()
                return@onSingleClick
            }
            if (preview == null || needUpdatePreview) {
                generatePreviewBitmap()
                needUpdatePreview = false
            }
            if (preview == null) {
                CustomerToast(this, "生成预览图片错误", Toast.LENGTH_SHORT).show()
            } else {
                showPreviewBitmapDialog(preview!!)
            }
        }
        binding.printBtn.onSingleClick {
            if (viewModel.getSelectData() == null) {
                CustomerToast(this, "未选择需要打印的数据", Toast.LENGTH_SHORT).show()
                return@onSingleClick
            }
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
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.label_bt_print)
    }

    @SuppressLint("MissingPermission")
    override fun setupEvent() {
        super.setupEvent()

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

    override fun start() {
        viewModel.refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_printer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.print -> {
                changeModel(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeModel(showBottom: Boolean) {
        searchDialog.dismiss()

        isShowBottom = showBottom
        viewModel.changeModel(showBottom)

        if (isShowBottom) {
            bottomAnim?.start()
        } else {
            bottomAnim?.reverse()
        }
    }

    private fun updateConnState() {
        viewModel.isConnected.value = LPAPIManager.isPrinterConnected()
    }

    private fun createAnimation() {
        bottomAnim = animSet {
            anim {
                values = intArrayOf(0, bottomLayoutHeight)
                action = { value ->
                    binding.bottomLayout.layoutParams.height = value as Int
                    binding.bottomLayout.requestLayout()
                }
            } with objectAnim {
                target = binding.search
                alpha = floatArrayOf(1f, 0.2f)
            }
            onStart = {
                binding.bottomLayout.visibility = View.VISIBLE
                binding.search.isClickable = false
            }
            onEnd = {
                binding.search.isClickable = true
            }
            duration = 300
            interpolator = DecelerateInterpolator()
        }
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

    // 显示打印预览图片
    private fun showPreviewBitmapDialog(bitmap: Bitmap) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("打印预览")
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        builder.setView(imageView)
        builder.setPositiveButton("确定") { _, _ -> printBitmap(bitmap) }
        builder.setNegativeButton("取消", null)
        builder.show()
    }

    private fun generatePreviewBitmap() {
        if (viewModel.getSelectData() != null) {
            val bitmap = createPrintContent(viewModel.getSelectData()!!)
            preview = LPAPIManager.drawBitmapAndQrCode(bitmap, "test test")
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
}