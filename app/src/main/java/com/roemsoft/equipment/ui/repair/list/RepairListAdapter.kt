package com.roemsoft.equipment.ui.repair.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemChildClickListener
import com.roemsoft.equipment.bean.RepairBean
import com.roemsoft.equipment.databinding.ItemRepairListBinding
import com.roemsoft.equipment.ui.OnItemClickListener
import com.roemsoft.equipment.widget.ExpandableTextView

class RepairListAdapter : BaseListAdapter<RepairBean, RepairListAdapter.RepairListViewHolder>() {

    private var expandSet = mutableSetOf<Int>()

    fun onItemClickListener(onClick: (String) -> Unit) {
        onItemClick = { _, index ->
            val data = list[index]
            onClick.invoke(data.no)
        }
    }

    fun onItemChildClickListener(onClick: (Int) -> Unit) {
        onItemChildClick = { view, index ->
            val etv = (view as ExpandableTextView)
            etv.toggleExpand()
            if (etv.isFolded) {
                expandSet.remove(index)
            } else {
                expandSet.add(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepairListViewHolder {
        return RepairListViewHolder.from(parent)
    }

    override fun convert(holder: RepairListViewHolder, item: RepairBean?) {
        item ?: return
        val isFolded = !expandSet.contains(holder.adapterPosition)
        holder.bind(item, isFolded, onItemClick, onItemChildClick)
    }

    class RepairListViewHolder private constructor(private val binding: ItemRepairListBinding) : BaseViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): RepairListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binging: ItemRepairListBinding = ItemRepairListBinding.inflate(layoutInflater, parent, false)
                return RepairListViewHolder(binging)
            }
        }

        // 先set方法设定值，再测量 布局
        // on create view holder 测量值为0
        fun bind(data: RepairBean, isFolded: Boolean, listener: OnItemClickListener?, childListener: OnItemChildClickListener?) {
            binding.data = data
            binding.isDefaultFolded = isFolded
            bindOnClickListener(listener)
            bindOnChildClickListener(binding.reason, childListener)
            binding.executePendingBindings()
        }
    }
}