package com.roemsoft.equipment.ui.archive.list

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.roemsoft.common.dialog.BottomContainerDialog
import com.roemsoft.common.dialog.bottomContainerDialog
import com.roemsoft.common.dialog.showListDialog
import com.roemsoft.common.dpToPx
import com.roemsoft.common.onSingleClick
import com.roemsoft.common.widget.MarginItemDecoration
import com.roemsoft.equipment.R
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.databinding.ActivityArchiveListBinding
import com.roemsoft.equipment.databinding.DialogArchiveListFilterBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.archive.detail.ArchiveDetailActivity
import timber.log.Timber

class ArchiveListActivity : DataBindingAppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "archive:extra:type"
        const val RESULT_SELECTED = "result:archive:selected"
        const val ACTION_SEARCH = 100001
    }

    private val binding: ActivityArchiveListBinding by binding(R.layout.activity_archive_list)

    override val viewModel: ArchiveListViewModel by viewModels()

    private val dialogBinding: DialogArchiveListFilterBinding by lazy {
        DataBindingUtil.inflate(layoutInflater, R.layout.dialog_archive_list_filter, null, false)
    }

    private lateinit var searchDialog: BottomContainerDialog

    private var action: Int = -1

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this

        action = intent.getIntExtra(EXTRA_TYPE, -1)
    }

    override fun initView() {
        searchDialog = bottomContainerDialog {
            view = {
                dialogBinding.confirm.onSingleClick {
                    viewModel.category.value = dialogBinding.category.text.toString().trim()
                    viewModel.name.value = dialogBinding.name.text.toString().trim()
                    viewModel.brand.value = dialogBinding.brand.text.toString().trim()
                    viewModel.spec.value = dialogBinding.spec.text.toString().trim()
                    viewModel.refreshData()
                    dismiss()
                }
                dialogBinding.cancel.onSingleClick {
                    dismiss()
                }
                dialogBinding.category.onSingleClick {
                    viewModel.showSelectCategoryDialog()
                }
                dialogBinding.root
            }
        }

        binding.refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        binding.refresh.setOnRefreshListener {
            viewModel.refreshData()
        }

        binding.list.apply {
            adapter = viewModel.adapter
            layoutManager = LinearLayoutManager(this@ArchiveListActivity)
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

        binding.addBtn.onSingleClick {
            addArchive()
        }
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.label_archive)
    }

    @SuppressLint("MissingPermission")
    override fun setupEvent() {
        super.setupEvent()

        viewModel.selectDialog.observe(this) {
            showListDialog {
                title = "选择类别名称"
                content = it
                allowClear = true
                onConfirm = { category ->
                    dialogBinding.category.text = category
                }
            }
        }

        viewModel.detail.observe(this) {
            when (action) {
                ACTION_SEARCH -> {
                    setResult(ACTION_SEARCH, Intent().apply {
                        putExtra(RESULT_SELECTED, it)
                    })
                    finish()
                }
                else -> archiveDetail(it)
            }
        }
    }

    override fun start() {
        viewModel.fetchCategoryList()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_archive_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                addArchive()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addArchive() {
        startActivity(Intent(this, ArchiveDetailActivity::class.java))
    }

    private fun archiveDetail(data: ArchiveData) {
        startActivity(Intent(this, ArchiveDetailActivity::class.java).apply {
            putExtra(ArchiveDetailActivity.EXTRA_DATA, data)
        })
    }
}