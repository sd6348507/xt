package com.roemsoft.equipment.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.bean.CostCompany
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class CostComSearchViewModel : BaseViewModel() {

    // 刷新
    private val _refresh = MutableLiveData(false)
    val refresh: LiveData<Boolean> = _refresh

    private val _select = MutableLiveData<CostCompany>()
    val select: LiveData<CostCompany> = _select

    // 源数据
    // 用于搜索过滤
    private var origList = listOf<CostCompany>()

    private val _searchFlow = MutableStateFlow("")

    val adapter = CostComSearchAdapter().apply {
        onClickResult { result ->
            _select.value = result
        }
    }

    val searchResult = _searchFlow
        .debounce(300) // 在指定时间内出现多个字符串，debounce 始终只会发出最后一个字符串
        .flatMapLatest { text ->
            // 只提供最后一个搜索查询（最新）的结果
            flow {
                if (text.isEmpty()) {
                    emit(origList)
                } else {
                    emit(origList.filter { it.name.contains(text, true) })
                }
            }
        }//.asLiveData()

    fun refreshData() {
        viewModelScope.launch {
            repository.fetchCostCompanyList()
                .onStart { _refresh.value = true }
                .catch { _refresh.value = false }
                .onCompletion { _refresh.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        origList = it.data
                        adapter.setData(origList)
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }
    }

    fun search(text: String) {
        _searchFlow.value = text
    }

    fun setAdapterData(list: List<CostCompany>) {
        adapter.setData(list)
    }
}