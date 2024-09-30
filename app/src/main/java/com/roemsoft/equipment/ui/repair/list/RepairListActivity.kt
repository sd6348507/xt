package com.roemsoft.equipment.ui.repair.list

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.dpToPx
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.MarginItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityRepairListBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.repair.add.AddRepairActivity
import com.roemsoft.equipment.ui.repair.detail.RepairDetailActivity
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter

class RepairListActivity : DataBindingAppCompatActivity() {

    private val mBinding: ActivityRepairListBinding by binding(R.layout.activity_repair_list)

    override val viewModel: RepairListViewModel by viewModels()

    override fun bindingView() {
        mBinding.vm = viewModel
        mBinding.lifecycleOwner = this
    }

    override fun initView() {
        mBinding.refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        mBinding.refresh.setOnRefreshListener {
            viewModel.refresh()
        }

        mBinding.list.apply {
            adapter = AlphaInAnimationAdapter(viewModel.adapter)
       //     itemAnimator = FadeInAnimator()
            addItemDecoration(MarginItemDecoration(dpToPx(8)))
        }

        mBinding.addBtn.onSingleClick {
            startActivity(Intent(this, AddRepairActivity::class.java))
        }
    }

    override fun getToolbar(): Toolbar {
        return mBinding.toolbar
    }

    override fun setToolTitle() {
        mBinding.toolbarTitle.setText(R.string.label_repair)
    }

    override fun setupEvent() {
        super.setupEvent()
        viewModel.detail.observe(this) {
            startActivity(Intent(this, RepairDetailActivity::class.java).apply {
                putExtra("code", it)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}