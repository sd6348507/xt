package com.roemsoft.equipment.ui.repair.add

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.onSingleClick
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityAddRepairBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.ScanActivity

class AddRepairActivity : DataBindingAppCompatActivity() {

    private val mBinding: ActivityAddRepairBinding by binding(R.layout.activity_add_repair)

    override val viewModel: AddRepairViewModel by viewModels()

    private lateinit var scanLauncher: ActivityResultLauncher<Intent>

    override fun bindingView() {
        mBinding.vm = viewModel
        mBinding.lifecycleOwner = this
    }

    override fun initView() {
        scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadData(result.data?.getStringExtra("scan:result") ?: "")
            }
        }

        mBinding.scanImg.onSingleClick {
            scanLauncher.launch(Intent(this, ScanActivity::class.java))
        }
    }

    override fun getToolbar(): Toolbar {
        return mBinding.toolbar
    }

    override fun setToolTitle() {
        mBinding.toolbarTitle.setText(R.string.label_add_repair)
    }

    override fun setupEvent() {
        super.setupEvent()

    }
}