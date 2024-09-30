package com.roemsoft.equipment.ui.lend.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.equipment.bean.LRListData
import com.roemsoft.equipment.databinding.ItemLendListContentBinding
import com.roemsoft.equipment.ui.OnItemClickListener

class LendListAdapter : BaseListAdapter<LRListData, LendListAdapter.LendListViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LendListViewHolder {
        return LendListViewHolder.from(parent)
    }

    override fun convert(holder: LendListViewHolder, item: LRListData?) {
        item ?: return
        holder.bind(item, holder.adapterPosition == selectPosition, itemClickListener)
    }

    class LendListViewHolder private constructor(private val binding: ItemLendListContentBinding) : BaseViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): LendListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binging: ItemLendListContentBinding = ItemLendListContentBinding.inflate(layoutInflater, parent, false)
                return LendListViewHolder(binging)
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