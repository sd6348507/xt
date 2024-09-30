package com.roemsoft.equipment.ui.printer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.equipment.bean.PrinterData
import com.roemsoft.equipment.databinding.ItemPrinterListBinding

class PrinterAdapter : BaseListAdapter<PrinterData, PrinterAdapter.PrinterViewHolder>() {

    private var isPrintModel = false

    private var selectPosition = -1

    private val itemClickListener: OnItemClickListener = { _, position ->
        if (selectPosition != position) {
            val last = selectPosition
            selectPosition = position
            notifyItemChanged(last)
            notifyItemChanged(position)
        }
    }

    fun getSelectData(): PrinterData? {
        return if (selectPosition < list.size && selectPosition >= 0) {
            list[selectPosition]
        } else {
            null
        }
    }

    fun printModel(isPrintModel: Boolean) {
        this.isPrintModel = isPrintModel
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrinterViewHolder =
        PrinterViewHolder.from(parent)

    override fun convert(holder: PrinterViewHolder, item: PrinterData?) {
        item ?: return
        holder.bind(item, isPrintModel, holder.adapterPosition == selectPosition, itemClickListener)
    }

    class PrinterViewHolder private constructor(private val binding: ItemPrinterListBinding) : BaseViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): PrinterViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPrinterListBinding.inflate(layoutInflater, parent, false)

                return PrinterViewHolder(binding)
            }
        }

        fun bind(data: PrinterData, isPrintModel: Boolean, isCheck: Boolean, listener: OnItemClickListener) {
            binding.data = data
            binding.isPrintModel = isPrintModel
            binding.isChecked = isCheck
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }

    }
}