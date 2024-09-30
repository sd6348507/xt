package com.roemsoft.equipment.ui.lend.add

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.dialog.ProgressBarDialog
import com.roemsoft.common.hideSoftKeyboard
import com.roemsoft.common.onSingleClick
import com.roemsoft.equipment.App
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityAddRepairBinding
import com.roemsoft.equipment.databinding.ActivityLendNewBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.ScanActivity
import com.roemsoft.equipment.ui.archive.list.ArchiveListActivity
import com.roemsoft.equipment.ui.search.CostComSearchActivity
import com.roemsoft.equipment.ui.search.DepartmentSearchActivity
import com.roemsoft.equipment.ui.search.PersonSearchActivity
import com.roemsoft.equipment.ui.search.ProviderSearchActivity
import com.roemsoft.zltd.ScannerUnitBroadcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

class LendNewActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityLendNewBinding by binding(R.layout.activity_lend_new)

    override val viewModel: LendNewViewModel by viewModels()

    private val dialog by lazy {
        ProgressBarDialog(this).build()
    }

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun initView() {
        if (App.isPda()) {
            binding.lendScanImg.visibility = View.INVISIBLE

            val scannerUnit = ScannerUnitBroadcast(this).apply {
                scannerResult.observe(this@LendNewActivity) { onScannerResult(it) }
            }

            lifecycle.addObserver(scannerUnit)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ProviderSearchActivity.ACTION_SEARCH_PROVIDER -> {
                    viewModel.provider.value = result.data?.getParcelableExtra(ProviderSearchActivity.RESULT_SELECTED)
                }
                ScanActivity.SCAN_SUCCESS -> {
                    val assetsNo = result.data?.getStringExtra(ScanActivity.SCAN_RESULT) ?: ""
                    viewModel.assetsNo.value = assetsNo
                    viewModel.loadData()
                }
            }
        }

        binding.lendSearchBtn.onSingleClick {
            viewModel.assetsNo.value = binding.lendSearchEt.text.trim().toString()
            viewModel.loadData()
        }

        binding.lendScanImg.onSingleClick {
            launcher.launch(Intent(this, ScanActivity::class.java))
        }

        binding.lendSource.onSingleClick {
            launcher.launch(Intent(this, ProviderSearchActivity::class.java))
        }

        binding.lendClear.onSingleClick {
            viewModel.clear()
        }

        binding.lendSubmitBtn.onSingleClick {
            viewModel.submit()
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.setText(R.string.label_lend)
    }

    override fun setupEvent() {
        super.setupEvent()

        viewModel.loading.observe(this) {
            if (it) {
                dialog.show()
            } else {
                dialog.hide()
            }
        }
    }

    private fun onScannerResult(code: String) {
        viewModel.assetsNo.value = code.trim()
        viewModel.loadData()
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