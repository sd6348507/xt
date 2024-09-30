package com.roemsoft.equipment.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.bean.Person
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class PersonSearchViewModel : BaseViewModel() {

    // 刷新
    private val _refresh = MutableLiveData(false)
    val refresh: LiveData<Boolean> = _refresh

    private val _select = MutableLiveData<String>()
    val select: LiveData<String> = _select

    // 源数据
    // 用于搜索过滤
    private var origList = listOf<Person>()

    private val _searchFlow = MutableStateFlow("")

    val adapter = PersonSearchAdapter().apply {
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
        }

    fun refreshData() {
        viewModelScope.launch {
            repository.fetchPersonList()
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

    fun setAdapterData(list: List<Person>) {
        adapter.setData(list)
    }
}