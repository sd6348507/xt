package com.roemsoft.equipment.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.CostCompany
import com.roemsoft.equipment.bean.ProviderData
import com.roemsoft.equipment.databinding.ItemTextSelectBinding

class ProviderSearchAdapter : BaseListAdapter<ProviderData, ProviderSearchAdapter.ProviderSearchViewHolder>() {

    fun onClickResult(block: (ProviderData) -> Unit) {
        onItemClick = { _, position ->
            val data = list[position]
            block.invoke(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderSearchViewHolder {
        return ProviderSearchViewHolder.from(parent)
    }

    override fun convert(holder: ProviderSearchViewHolder, item: ProviderData?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class ProviderSearchViewHolder private constructor(private val binding: ItemTextSelectBinding): BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ProviderSearchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTextSelectBinding.inflate(inflater, parent, false)
                return ProviderSearchViewHolder(binding)
            }
        }

        fun bind(data: ProviderData, listener: OnItemClickListener?) {
            binding.text = data.name
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}