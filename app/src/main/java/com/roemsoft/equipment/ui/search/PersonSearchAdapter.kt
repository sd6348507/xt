package com.roemsoft.equipment.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.Person
import com.roemsoft.equipment.databinding.ItemTextSelectBinding

class PersonSearchAdapter : BaseListAdapter<Person, PersonSearchAdapter.PersonSearchViewHolder>() {

    fun onClickResult(block: (String) -> Unit) {
        onItemClick = { _, position ->
            val data = list[position].name
            block.invoke(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonSearchViewHolder {
        return PersonSearchViewHolder.from(parent)
    }

    override fun convert(holder: PersonSearchViewHolder, item: Person?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class PersonSearchViewHolder private constructor(private val binding: ItemTextSelectBinding): BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): PersonSearchViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemTextSelectBinding.inflate(inflater, parent, false)
                return PersonSearchViewHolder(binding)
            }
        }

        fun bind(data: Person, listener: OnItemClickListener?) {
            binding.text = data.name
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }
    }
}