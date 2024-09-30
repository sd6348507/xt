package com.roemsoft.equipment.ui.retur.list

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.SpaceItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityReturnListBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.retur.add.ReturnNewActivity

class ReturnListActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityReturnListBinding by binding(R.layout.activity_return_list)

    override val viewModel: ReturnListViewModel by viewModels()

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {
        binding.returnListRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        binding.returnListRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.returnList.apply {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(SpaceItemDecoration(context))
            adapter = viewModel.adapter
        }

        binding.returnListTitleScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.returnListContentScroll.scrollTo(scrollX, scrollY)
        }
        binding.returnListContentScroll.setOnScrollChangeListener { _, scrollX, scrollY, _, _ ->
            binding.returnListTitleScroll.scrollTo(scrollX, scrollY)
        }

        binding.returnListNew.onSingleClick {
            startActivity(Intent(this, ReturnNewActivity::class.java))
        }

        binding.returnListSubmit.onSingleClick {
            viewModel.submit()
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.setText(R.string.label_return_review)
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