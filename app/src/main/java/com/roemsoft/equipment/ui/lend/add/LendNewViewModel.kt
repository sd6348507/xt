package com.roemsoft.equipment.ui.lend.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.common.preference.Preference
import com.roemsoft.equipment.App
import com.roemsoft.equipment.bean.ArchiveBaseData
import com.roemsoft.equipment.bean.CostCompany
import com.roemsoft.equipment.bean.ProviderData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LendNewViewModel : BaseViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    val assetsNo = MutableLiveData("")

    val data = MutableLiveData<ArchiveBaseData>()

    val provider = MutableLiveData<ProviderData>()

    val qty = MutableLiveData("1")

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
        if (App.userCode.isNullOrEmpty()) {
            showToast("请先登录")
            return
        }
        if (provider.value == null) {
            showToast("请选择借出单位")
            return
        }

        if (assetsNo.value.isNullOrEmpty() || data.value == null || data.value?.archiveNo.isNullOrEmpty()) {
            showToast("请先输入设备信息")
            return
        }
        if (qty.value.isNullOrEmpty()) {
            showToast("请输入数量")
            return
        }

        viewModelScope.launch {
            repository.lendSubmit(data.value!!.costComNo, provider.value!!.name, assetsNo.value!!,
                data.value!!.department, data.value!!.person, data.value!!.area,
                data.value!!.archiveNo, qty.value!!, App.userCode!!)
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        showToast("提交成功")
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }

        }
    }

    fun clear() {
        provider.value = ProviderData()
        data.value = ArchiveBaseData()
        qty.value = "1"
        assetsNo.value = ""
    }
}