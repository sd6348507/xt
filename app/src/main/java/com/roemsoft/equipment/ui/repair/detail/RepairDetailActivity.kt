package com.roemsoft.equipment.ui.repair.detail

import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityRepairDetailBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity

class RepairDetailActivity : DataBindingAppCompatActivity() {

    private val mBinding: ActivityRepairDetailBinding by binding(R.layout.activity_repair_detail)

    override val viewModel: RepairDetailViewModel by viewModels()

    override fun bindingView() {
        mBinding.vm = viewModel
        mBinding.lifecycleOwner = this
    }

    override fun initView() {
        val code = intent.getStringExtra("code") ?: ""
        viewModel.loadData(code)
    }

    override fun getToolbar(): Toolbar {
        return mBinding.toolbar
    }

    override fun setToolTitle() {
        mBinding.toolbarTitle.setText(R.string.label_repair_detail)
    }

    override fun setupEvent() {
        super.setupEvent()

    }
}