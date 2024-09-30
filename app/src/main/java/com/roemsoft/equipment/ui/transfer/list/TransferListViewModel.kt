package com.roemsoft.equipment.ui.transfer.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.App
import com.roemsoft.equipment.bean.TransferListData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TransferListViewModel : BaseViewModel() {

    val loading = ObservableBoolean()

    val adapter = TransferListAdapter()

    fun loadData() {
        if (App.userCode.isNullOrEmpty()) {
            showToast("请先登录")
            return
        }
        viewModelScope.launch {
            repository.fetchTransferList(App.userCode!!)
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