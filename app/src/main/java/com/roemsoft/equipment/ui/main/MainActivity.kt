package com.roemsoft.equipment.ui.main

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.roemsoft.common.onSingleClick
import com.roemsoft.equipment.App
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityMainBinding
import com.roemsoft.equipment.ui.BaseActivity
import com.roemsoft.equipment.ui.archive.list.ArchiveListActivity
import com.roemsoft.equipment.ui.checkin.CheckInActivity
import com.roemsoft.equipment.ui.lend.list.LendListActivity
import com.roemsoft.equipment.ui.printer.PrinterActivity
import com.roemsoft.equipment.ui.repair.list.RepairListActivity
import com.roemsoft.equipment.ui.retur.list.ReturnListActivity
import com.roemsoft.equipment.ui.search.scan.AssetsScanActivity
import com.roemsoft.equipment.ui.transfer.TransferActivity
import com.roemsoft.equipment.ui.transfer.list.TransferListActivity
import com.roemsoft.equipment.ui.update.UpdateActivity
import com.roemsoft.zltd.ScannerUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun bindingView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun setToolTitle() {
        binding.toolbarTitle.text = getString(R.string.app_name)
    }

    override fun initToolbar() {
        super.initToolbar()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun initView() {
        binding.imgRepair.onSingleClick {
            startActivity(
                Intent(this, RepairListActivity::class.java)
            )
        }
        binding.imgLend.onSingleClick {
            startActivity(
                Intent(this, LendListActivity::class.java)
            )
        }
        binding.imgReturn.onSingleClick {
            startActivity(
                Intent(this, ReturnListActivity::class.java)
            )
        }
        binding.imgTransfer.onSingleClick {
            startActivity(
                Intent(this, TransferListActivity::class.java)
            )
        }
        binding.imgPrinter.onSingleClick {
            startActivity(
                Intent(this, PrinterActivity::class.java)
            )
        }
        binding.imgArchive.onSingleClick {
            startActivity(
                Intent(this, ArchiveListActivity::class.java)
            )
        }

        binding.imgCheckIn.onSingleClick {
            startActivity(
                Intent(this, CheckInActivity::class.java)
            )
        }

        binding.imgAssetsSearch.onSingleClick {
            startActivity(
                Intent(this, AssetsScanActivity::class.java)
            )
        }
    }

    override fun start() {
        if (App.isPda()) {
            ScannerUnit.init()
        }
    }

    override fun onDestroy() {
        if (App.isPda()) {
            ScannerUnit.release()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                startActivity(Intent(this, UpdateActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}