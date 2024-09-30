package com.roemsoft.equipment.ui.transfer.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.bean.TransferListData
import com.roemsoft.equipment.databinding.ItemTransferListContentBinding

class TransferListAdapter : BaseListAdapter<TransferListData, TransferListAdapter.TransferListViewHolder>() {

    private var selectPosition = -1

    private val itemClickListener: OnItemClickListener = { _, position ->
        if (selectPosition != position) {
            val last = selectPosition
            selectPosition = position
            notifyItemChanged(last)
            notifyItemChanged(position)
        }
    }

    fun getSelectData(): TransferListData? {
        return if (selectPosition < list.size && selectPosition >= 0) {
            list[selectPosition]
        } else {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransferListViewHolder {
        return TransferListViewHolder.from(parent)
    }

    override fun convert(holder: TransferListViewHolder, item: TransferListData?) {
        item ?: return
        holder.bind(item, holder.adapterPosition == selectPosition, itemClickListener)
    }

    class TransferListViewHolder private constructor(private val binding: ItemTransferListContentBinding) : BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TransferListViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding: ItemTransferListContentBinding = ItemTransferListContentBinding.inflate(inflater, parent, false)
                return TransferListViewHolder(binding)
            }
        }

        fun bind(data: TransferListData, isCheck: Boolean, listener: OnItemClickListener?) {
            binding.data = data
            binding.isChecked = isCheck
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}