package com.roemsoft.equipment.ui.archive.detail

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.dialog.ProgressBarDialog
import com.roemsoft.common.dialog.showListDialog
import com.roemsoft.common.hideSoftKeyboard
import com.roemsoft.common.onSingleClick
import com.roemsoft.equipment.R
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.databinding.ActivityArchiveDetailBinding
import com.roemsoft.equipment.databinding.ActivityArchiveListBinding
import com.roemsoft.equipment.databinding.ActivityRepairDetailBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import timber.log.Timber

class ArchiveDetailActivity : DataBindingAppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "archive:extra:data"
    }

    private val mBinding: ActivityArchiveDetailBinding by binding(R.layout.activity_archive_detail)

    override val viewModel: ArchiveDetailViewModel by viewModels()

    private val dialog by lazy {
        ProgressBarDialog(this).build()
    }

    private var isAdd: Boolean = true

    override fun bindingView() {
        mBinding.vm = viewModel
        mBinding.lifecycleOwner = this
    }

    override fun initView() {
        mBinding.category.onSingleClick {
            viewModel.showSelectCategoryDialog()
        }

        mBinding.labelNo.visibility = if (isAdd) View.GONE else View.VISIBLE
        mBinding.no.visibility = if (isAdd) View.GONE else View.VISIBLE
        mBinding.dividerH2.visibility = if (isAdd) View.GONE else View.VISIBLE

        mBinding.submit.text = if (isAdd) "新建" else "修改"
        mBinding.bottomLayout.onSingleClick {
            viewModel.submit()
        }

        if (!isAdd) {
            viewModel.data.value = intent.getParcelableExtra(EXTRA_DATA)
        } else {
            viewModel.data.value = ArchiveData()
        }
    }

    override fun getToolbar(): Toolbar {
        return mBinding.toolbar
    }

    override fun setToolTitle() {
        isAdd = intent.getParcelableExtra<ArchiveData>(EXTRA_DATA) == null
        if (isAdd) {
            mBinding.toolbarTitle.setText(R.string.label_archive_add)
        } else {
            mBinding.toolbarTitle.setText(R.string.label_archive_detail)
        }
    }

    override fun start() {
        viewModel.fetchCategoryList()
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

        viewModel.selectDialog.observe(this) {
            showListDialog {
                title = "选择类别名称"
                content = it
                allowClear = false
                onConfirm = { category ->
                    mBinding.category.text = category
                }
            }
        }

        viewModel.result.observe(this) {
            if (isAdd && it) {
                viewModel.data.value = ArchiveData()
            }
        }

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