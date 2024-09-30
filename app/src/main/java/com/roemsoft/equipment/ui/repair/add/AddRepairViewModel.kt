package com.roemsoft.equipment.ui.repair.add

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roemsoft.equipment.bean.Equipment
import com.roemsoft.equipment.bean.RepairBean
import com.roemsoft.equipment.ui.BaseViewModel

class AddRepairViewModel : BaseViewModel() {

    val loading = ObservableBoolean()

    val input = MutableLiveData<RepairBean>()

    fun loadData(code: String) {

    }

    fun submit() {

    }
}