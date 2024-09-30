package com.roemsoft.equipment.ui.archive.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ArchiveListViewModel : BaseViewModel() {

    val isEmpty = MutableLiveData(true)

    val loading = ObservableBoolean()

    // 刷新
  //  private val _refresh = MutableLiveData(false)
  //  val refresh: LiveData<Boolean> = _refresh

    val category = MutableLiveData("")

    val name = MutableLiveData("")

    val brand = MutableLiveData("")

    val spec = MutableLiveData("")

    private val _detail = MutableLiveData<ArchiveData>()
    val detail: LiveData<ArchiveData> = _detail

    private val _selectDialog = MutableLiveData<ArrayList<String>>()
    val selectDialog: LiveData<ArrayList<String>> = _selectDialog

    val adapter = ArchiveListAdapter().apply {
        onItemClickListener {
            _detail.value = it
        }
    }

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

    fun refreshData() {
        viewModelScope.launch {
            repository.fetchArchiveList(category.value!!, name.value!!, brand.value!!, spec.value!!)
                .onStart {
                    loading.set(true)
                }
                .catch {
                    loading.set(false)
                }
                .onCompletion {
                    loading.set(false)
                }
                .collectLatest { result ->
                    result.doSuccess {
                        adapter.setData(it.data)
                        isEmpty.value = it.data.isEmpty()
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