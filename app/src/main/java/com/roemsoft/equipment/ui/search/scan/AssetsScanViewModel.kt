package com.roemsoft.equipment.ui.search.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.equipment.bean.AssetsData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AssetsScanViewModel : BaseViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    val data = MutableLiveData<AssetsData>()

    val assetsNo = MutableLiveData("")

    fun loadData(code: String = assetsNo.value!!) {
        viewModelScope.launch {
            repository.assetsSearch(code)
            .onStart { _loading.value = true }
            .catch { _loading.value = false }
            .onCompletion { _loading.value = false }
            .collectLatest { result ->
                result.doSuccess {
                    if (it.data.isEmpty()) {
                        data.value = AssetsData()
                    } else {
                        data.value = it.data[0]
                    }
                }
                result.doFailure { showToast(it) }
                result.doError { showToast(it) }
            }
        }
    }
}