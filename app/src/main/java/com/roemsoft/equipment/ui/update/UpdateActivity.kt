package com.roemsoft.equipment.ui.update

import android.Manifest
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.permissionx.guolindev.PermissionX
import com.roemsoft.common.dialog.showAlertDialog
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityUpdateBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity

class UpdateActivity : DataBindingAppCompatActivity() {

    private val binding: ActivityUpdateBinding by binding(R.layout.activity_update)

    override val viewModel: UpdateViewModel by viewModels()

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {
        viewModel.checkVersion()
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.action_update)
    }

    override fun setupEvent() {
        super.setupEvent()
        viewModel.showUpdateDialog.observe(this) {
            if (it) {
                showUpdateDialog()
            }
        }
    }

    private fun requestDownloadPermissions() {
        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "同意申请Apk文件写入权限", Toast.LENGTH_SHORT).show()
                    binding.updateProgressGroup.visibility = View.VISIBLE
                    viewModel.requestDownloadApk()
                } else {
                    Toast.makeText(this, "申请Apk文件写入权限被拒绝：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showUpdateDialog() {
        showAlertDialog {
            title = "更新提醒"
            content = "当前有新版本，是否立即更新？"
            onConfirm = {
                requestDownloadPermissions()
            }
        }
    }
}