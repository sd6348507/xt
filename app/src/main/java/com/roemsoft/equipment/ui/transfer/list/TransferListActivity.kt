package com.roemsoft.equipment.ui.transfer.list

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.SpaceItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityTransferListBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.transfer.TransferActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class TransferListActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityTransferListBinding by binding(R.layout.activity_transfer_list)

    override val viewModel: TransferListViewModel by viewModels()

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.label_transfer_review)
    }

    override fun initView() {
        binding.transferList.run {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(SpaceItemDecoration(context))
            adapter = viewModel.adapter
        }

        binding.transferListRefresh.run {
            setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        }
        binding.transferListRefresh.setOnRefreshListener {
            viewModel.loadData()
        }

        binding.transferListTitleScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.transferListContentScroll.scrollTo(scrollX, scrollY)
        }
        binding.transferListContentScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.transferListTitleScroll.scrollTo(scrollX, scrollY)
        }

        binding.transferListNew.onSingleClick {
            startActivity(Intent(this, TransferActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }
}