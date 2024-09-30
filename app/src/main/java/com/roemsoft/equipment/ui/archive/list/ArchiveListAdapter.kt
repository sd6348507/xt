package com.roemsoft.equipment.ui.archive.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemChildClickListener
import com.roemsoft.equipment.bean.ArchiveData
import com.roemsoft.equipment.bean.RepairBean
import com.roemsoft.equipment.databinding.ItemArchiveListBinding
import com.roemsoft.equipment.databinding.ItemRepairListBinding
import com.roemsoft.equipment.ui.OnItemClickListener
import com.roemsoft.equipment.widget.ExpandableTextView

class ArchiveListAdapter : BaseListAdapter<ArchiveData, ArchiveListAdapter.ArchiveListViewHolder>() {

    fun onItemClickListener(onClick: (ArchiveData) -> Unit) {
        onItemClick = { _, index ->
            val data = list[index]
            onClick.invoke(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveListViewHolder {
        return ArchiveListViewHolder.from(parent)
    }

    override fun convert(holder: ArchiveListViewHolder, item: ArchiveData?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class ArchiveListViewHolder private constructor(private val binding: ItemArchiveListBinding) : BaseViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ArchiveListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binging: ItemArchiveListBinding = ItemArchiveListBinding.inflate(layoutInflater, parent, false)
                return ArchiveListViewHolder(binging)
            }
        }

        // 先set方法设定值，再测量 布局
        // on create view holder 测量值为0
        fun bind(data: ArchiveData, listener: OnItemClickListener?) {
            binding.data = data
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}