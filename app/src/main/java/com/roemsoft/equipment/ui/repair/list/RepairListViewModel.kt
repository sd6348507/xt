package com.roemsoft.equipment.ui.repair.list

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roemsoft.equipment.bean.RepairBean
import com.roemsoft.equipment.ui.BaseViewModel

class RepairListViewModel : BaseViewModel() {

    val loading = ObservableBoolean()

    private val _detail = MutableLiveData<String>()
    val detail: LiveData<String> = _detail
    private val _expandIndex = MutableLiveData<Int>()
    val expandIndex: LiveData<Int> = _expandIndex

    val adapter = RepairListAdapter().apply {
        onItemClickListener {
            _detail.value = it
        }
        onItemChildClickListener { index ->
            _expandIndex.value = index
        }
    }

    fun refresh() {
        val list = arrayListOf<RepairBean>()
        list.add(RepairBean(
            no = "no",
            price = "price",
            date = "1900-88-88",
            count = "",
            name = "name ",
            firm = "firm",
            total = "total",
            state = "unit",
            reason = "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444442222222222"
        ))
        for (i in 1..20) {
            list.add(RepairBean(
                no = "no $i",
                price = "price $i",
                date = "1900-88-88",
                count = "$i",
                name = "name $i",
                firm = "firm $i",
                total = "total $i",
                state = "unit $i",
                reason = "$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i$i"
            ))
        }
        list.add(RepairBean(
            no = "no",
            price = "price",
            date = "1900-88-88",
            count = "",
            name = "name ",
            firm = "firm",
            total = "total",
            state = "unit",
            reason = ""
        ))
        adapter.setData(list)

        loading.set(false)
    }
}