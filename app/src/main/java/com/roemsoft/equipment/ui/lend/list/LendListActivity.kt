package com.roemsoft.equipment.ui.lend.list

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.SpaceItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityLendListBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.lend.add.LendNewActivity

class LendListActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityLendListBinding by binding(R.layout.activity_lend_list)

    override val viewModel: LendListViewModel by viewModels()

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {
        binding.lendListRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        binding.lendListRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.lendList.apply {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(SpaceItemDecoration(context))
            adapter = viewModel.adapter
        }

        binding.lendListTitleScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.lendListContentScroll.scrollTo(scrollX, scrollY)
        }
        binding.lendListContentScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.lendListTitleScroll.scrollTo(scrollX, scrollY)
        }

        binding.lendListNew.onSingleClick {
            startActivity(Intent(this, LendNewActivity::class.java))
        }

        binding.lendListSubmit.onSingleClick {
            viewModel.submit()
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.setText(R.string.label_lend_review)
    }

    override fun setupEvent() {
        super.setupEvent()

        viewModel.update.observe(this) {
            if (it) {
                viewModel.refresh()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}