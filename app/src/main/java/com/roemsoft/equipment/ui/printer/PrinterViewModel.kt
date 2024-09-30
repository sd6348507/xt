package com.roemsoft.equipment.ui.printer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.today
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PrinterViewModel : BaseViewModel() {

    // 刷新
    private val _refresh = MutableLiveData(false)
    val refresh: LiveData<Boolean> = _refresh

    val isConnected = MutableLiveData(false)

    val no = MutableLiveData("")

    val name = MutableLiveData("")

    val department = MutableLiveData("")

    val area = MutableLiveData("")

    val worker = MutableLiveData("")

    val adapter = PrinterAdapter()

    fun refreshData() {
        /*viewModelScope.launch {

            repository.updateCpkcList(store.value!!, itemNo.value!!, itemName.value!!)
                .onStart {
                    _refresh.value = true
                }
                .catch {
                    _refresh.value = false
                }
                .onCompletion {
                    _refresh.value = false
                }
                .collectLatest { result ->
                    result.doSuccess {
                        adapter.setData(it.data)
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }*/
        val list = arrayListOf<PrinterData>()
        for (i in 0..10) {
            list.add(
                PrinterData("no$i", "name$i", "department$i", "worker$i",
                    "area$i", "spec$i", today()
                )
            )
        }
        adapter.setData(list)
    }

    fun updateConnectState(isConn: Boolean) {
        isConnected.value = isConn
    }

    fun changeModel(isPrintModel: Boolean) {
        adapter.printModel(isPrintModel)
    }

    fun getSelectData(): PrinterData? {
        return adapter.getSelectData()
    }
}