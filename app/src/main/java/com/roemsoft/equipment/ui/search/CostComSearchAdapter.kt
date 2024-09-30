package com.roemsoft.equipment.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.CostCompany
import com.roemsoft.equipment.databinding.ItemTextSelectBinding

class CostComSearchAdapter : BaseListAdapter<CostCompany, CostComSearchAdapter.CostComSearchViewHolder>() {

    fun onClickResult(block: (CostCompany) -> Unit) {
        onItemClick = { _, position ->
            val data = list[position]
            block.invoke(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostComSearchViewHolder {
        return CostComSearchViewHolder.from(parent)
    }

    override fun convert(holder: CostComSearchViewHolder, item: CostCompany?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class CostComSearchViewHolder private constructor(private val binding: ItemTextSelectBinding): BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): CostComSearchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTextSelectBinding.inflate(inflater, parent, false)
                return CostComSearchViewHolder(binding)
            }
        }

        fun bind(data: CostCompany, listener: OnItemClickListener?) {
            binding.text = data.name
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}