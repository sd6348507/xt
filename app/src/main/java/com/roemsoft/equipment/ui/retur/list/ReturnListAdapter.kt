package com.roemsoft.equipment.ui.retur.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.equipment.bean.LRListData
import com.roemsoft.equipment.databinding.ItemReturnListContentBinding
import com.roemsoft.equipment.ui.OnItemClickListener

class ReturnListAdapter : BaseListAdapter<LRListData, ReturnListAdapter.ReturnListViewHolder>() {

    private var selectPosition = -1

    private val itemClickListener: OnItemClickListener = { _, position ->
        if (selectPosition != position) {
            val last = selectPosition
            selectPosition = position
            notifyItemChanged(last)
            notifyItemChanged(position)
        }
    }

    fun getSelectData(): LRListData? {
        return if (selectPosition < list.size && selectPosition >= 0) {
            list[selectPosition]
        } else {
            null
        }
    }

    fun clearSelected() {
        selectPosition = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReturnListViewHolder {
        return ReturnListViewHolder.from(parent)
    }

    override fun convert(holder: ReturnListViewHolder, item: LRListData?) {
        item ?: return
        holder.bind(item, holder.adapterPosition == selectPosition, itemClickListener)
    }

    class ReturnListViewHolder private constructor(private val binding: ItemReturnListContentBinding) : BaseViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ReturnListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binging: ItemReturnListContentBinding = ItemReturnListContentBinding.inflate(layoutInflater, parent, false)
                return ReturnListViewHolder(binging)
            }
        }

        fun bind(data: LRListData, isCheck: Boolean, listener: OnItemClickListener?) {
            binding.data = data
            binding.isChecked = isCheck
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}