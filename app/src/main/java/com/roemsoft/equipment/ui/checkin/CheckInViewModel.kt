package com.roemsoft.equipment.ui.checkin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.common.preference.Preference
import com.roemsoft.common.today
import com.roemsoft.equipment.App
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.bean.CostCompany
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber

class CheckInViewModel : BaseViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _needUpdatePreview = MutableLiveData(false)
    val needUpdatePreview: LiveData<Boolean> = _needUpdatePreview


    val archive = MutableLiveData<ArchiveData>()

    val costCom = MutableLiveData<CostCompany>()
    val no = MutableLiveData("")
    val department = MutableLiveData("")
    val person = MutableLiveData("")
    val area = MutableLiveData("")

    val qty = MutableLiveData("1")
    val source = MutableLiveData("")

    // 蓝牙连接
    val isConnected = MutableLiveData(false)

    fun submit() {
        Timber.tag("username").i("App${App.userCode}")
        if (App.userCode.isNullOrEmpty()) {
            showToast("请先登录")
            return
        }
        if (costCom.value?.name.isNullOrEmpty()) {
            showToast("请选择归属公司")
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
        if (archive.value == null || archive.value?.no.isNullOrEmpty()) {
            showToast("请选择设备档案")
            return
        }
        if (qty.value.isNullOrEmpty()) {
            showToast("请输入数量")
            return
        }

        viewModelScope.launch {
            repository.submitCheckIn(costCom.value!!.no, "", department.value!!, person.value!!,
                area.value!!, archive.value!!.no, qty.value!!, source.value!!, App.userCode!!)
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        showToast("提交成功")
                        no.value = it.no
                        _needUpdatePreview.value = true
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }

        }
    }

    fun getSelectData(): PrinterData? {
        return if (no.value.isNullOrEmpty()) {
            null
        } else {
            PrinterData(no.value!!, archive.value?.name ?: "", department.value!!, person.value!!,
                area.value!!, archive.value?.spec ?: "", today()
            )
        }
    }

    fun clear() {
        costCom.value = null
        no.value = ""
        department.value = ""
        person.value = ""
        area.value = ""
        archive.value = null
        qty.value = "1"
        source.value = ""
    }
}