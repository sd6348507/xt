package com.roemsoft.equipment.ui.bt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.databinding.ItemBluetoothDeviceListBinding

@SuppressLint("MissingPermission")
class BluetoothDeviceAdapter : BaseListAdapter<BluetoothDevice, BluetoothDeviceAdapter.BluetoothDeviceViewHolder>() {

    override fun addData(data: BluetoothDevice) {
        for (item in list) {
            if (item.name == data.name && item.address == data.address) {
                return
            }
        }
        super.addData(data)
    }

    fun onItemClickResult(block: (View, Int, BluetoothDevice) -> Unit) {
        onItemClick = { v, position ->
            val data = list[position]
            block.invoke(v, position, data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder.from(parent)
    }

    override fun convert(holder: BluetoothDeviceViewHolder, item: BluetoothDevice?) {
        item ?: return
        holder.bind(item, onItemClick)
    }


    class BluetoothDeviceViewHolder private constructor(private val binding: ItemBluetoothDeviceListBinding) : BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): BluetoothDeviceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBluetoothDeviceListBinding.inflate(layoutInflater, parent, false)

                return BluetoothDeviceViewHolder(binding)
            }
        }

        fun bind(device: BluetoothDevice, listener: OnItemClickListener?) {
            val name = if (device.name.isNullOrEmpty()) {
                device.address
            } else {
                device.name + " " + device.address
            }
            binding.name.text = name
            bindOnClickListener(listener)
        }
    }
}