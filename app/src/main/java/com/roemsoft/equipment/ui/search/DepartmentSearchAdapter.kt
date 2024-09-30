package com.roemsoft.equipment.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.Department
import com.roemsoft.equipment.databinding.ItemTextSelectBinding

class DepartmentSearchAdapter : BaseListAdapter<Department, DepartmentSearchAdapter.DepartmentSearchViewHolder>() {

    fun onClickResult(block: (String) -> Unit) {
        onItemClick = { _, position ->
            val data = list[position].name
            block.invoke(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentSearchViewHolder {
        return DepartmentSearchViewHolder.from(parent)
    }

    override fun convert(holder: DepartmentSearchViewHolder, item: Department?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class DepartmentSearchViewHolder private constructor(private val binding: ItemTextSelectBinding): BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): DepartmentSearchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTextSelectBinding.inflate(inflater, parent, false)
                return DepartmentSearchViewHolder(binding)
            }
        }

        fun bind(data: Department, listener: OnItemClickListener?) {
            binding.text = data.name
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}