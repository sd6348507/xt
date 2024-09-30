package com.roemsoft.equipment.ui.archive.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.App
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ArchiveDetailViewModel : BaseViewModel() {

    val data = MutableLiveData<ArchiveData>()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _selectDialog = MutableLiveData<ArrayList<String>>()
    val selectDialog: LiveData<ArrayList<String>> = _selectDialog

    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> = _result

    private val categoryArray = arrayListOf<String>()

    // 获取设备类别名称列表
    fun fetchCategoryList() {
        viewModelScope.launch {
            repository.fetchCategoryList()
                .collectLatest { result ->
                    result.doSuccess { dataSet ->
                        categoryArray.clear()
                        dataSet.data.forEach { categoryArray.add(it.name) }
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
        if (data.value?.category.isNullOrEmpty()) {
            showToast("请选择类别名称")
            return
        }
        if (data.value?.name.isNullOrEmpty()) {
            showToast("请输入设备名称")
            return
        }
        if (data.value?.unit.isNullOrEmpty()) {
            showToast("请输入计量单位")
            return
        }

        viewModelScope.launch {
            repository.submitArchive(data.value!!.category, data.value!!.no, data.value!!.name,
                data.value!!.brand, data.value!!.spec, data.value!!.unit, App.userCode!!)
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        showToast("提交成功")
                        _result.value = true
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }
    }

    fun showSelectCategoryDialog() {
        _selectDialog.value = categoryArray
    }
}