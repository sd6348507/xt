package com.roemsoft.equipment.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.common.preference.Preference
import com.roemsoft.common.today
import com.roemsoft.equipment.App
import com.roemsoft.equipment.bean.ArchiveBaseData
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class TransferViewModel : BaseViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _needUpdatePreview = MutableLiveData(false)
    val needUpdatePreview: LiveData<Boolean> = _needUpdatePreview

    val assetsNo = MutableLiveData("")

    val data = MutableLiveData<ArchiveBaseData>()

    val department = MutableLiveData("")
    val person = MutableLiveData("")
    val area = MutableLiveData("")

    val qty = MutableLiveData("1")

    // 蓝牙连接
    val isConnected = MutableLiveData(false)

    fun loadData() {
        viewModelScope.launch {
            repository.archiveBaseInfoSearch(assetsNo.value!!)
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        if (it.data.isEmpty()) {
                            data.value = ArchiveBaseData()
                        } else {
                            data.value = it.data[0]
                        }
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }
    }

    fun submit() {
        Timber.tag("username").i("App${App.userCode}")
        if (App.userCode.isNullOrEmpty()) {
            showToast("请先登录")
            return
        }
        if (assetsNo.value.isNullOrEmpty() || data.value == null || data.value?.archiveNo.isNullOrEmpty()) {
            showToast("请先输入设备信息")
            return
        }
        if (department.value.isNullOrEmpty()) {
            showToast("请选择责任部门")
            return
        }
        if (person.value.isNullOrEmpty()) {
            showToast("请选择责任人")
            return
        }
        if (area.value.isNullOrEmpty()) {
            showToast("请输入存放位置")
            return
        }
        if (qty.value.isNullOrEmpty()) {
            showToast("请输入数量")
            return
        }

        viewModelScope.launch {
            repository.transferSubmit(data.value!!.costComNo, assetsNo.value!!,
                data.value!!.department, data.value!!.person, data.value!!.area,
                department.value!!, person.value!!, area.value!!, data.value!!.archiveNo, qty.value!!, App.userCode!!)
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        showToast("提交成功")
                        _needUpdatePreview.value = true
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }

        }
    }

    fun getSelectData(): PrinterData? {
        return if (assetsNo.value.isNullOrEmpty() || data.value == null || data.value?.archiveNo.isNullOrEmpty()) {
            null
        } else {
            PrinterData(assetsNo.value!!, data.value?.name ?: "", department.value!!, person.value!!,
                area.value!!, data.value?.spec ?: "", today()
            )
        }
    }

    fun clear() {
        department.value = ""
        person.value = ""
        area.value = ""
        data.value = ArchiveBaseData()
        qty.value = "1"
        assetsNo.value = ""
    }
}