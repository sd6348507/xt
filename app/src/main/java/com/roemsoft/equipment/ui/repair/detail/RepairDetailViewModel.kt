package com.roemsoft.equipment.ui.repair.detail

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.roemsoft.equipment.bean.Equipment
import com.roemsoft.equipment.bean.RepairBean
import com.roemsoft.equipment.ui.BaseViewModel

class RepairDetailViewModel : BaseViewModel() {

    val loading = ObservableBoolean()


    val data = MutableLiveData<Equipment>()

    val input = MutableLiveData<RepairBean>()

    fun loadData(code: String) {

    }

    fun submit() {

    }
}