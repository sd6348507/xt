package com.roemsoft.equipment.ui.lend.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LendListViewModel : BaseViewModel() {

    val loading = ObservableBoolean()

    val adapter = LendListAdapter()

    fun refresh() {
        viewModelScope.launch {
            repository.fetchLendList()
                .onStart { loading.set(true) }
                .catch { loading.set(false) }
                .onCompletion { loading.set(false) }
                .collectLatest { result ->
                    result.doSuccess {
                        adapter.setData(it.data)
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }
    }
}