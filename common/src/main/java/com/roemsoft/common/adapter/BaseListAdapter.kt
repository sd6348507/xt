package com.roemsoft.common.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

typealias OnItemClickListener = (View, Int) -> Unit
typealias OnItemChildClickListener = (View, Int) -> Unit
typealias OnItemLongClickListener = (View, Int) -> Unit

abstract class BaseListAdapter<T, K: BaseViewHolder> : RecyclerView.Adapter<K>() {

    var list = arrayListOf<T>()

    var onItemClick: OnItemClickListener? = null

    var onItemChildClick: OnItemChildClickListener? = null

    var onItemLongClickListener: OnItemLongClickListener? = null

    fun onClickResult(block: (T, Int) -> Unit) {
        onItemClick = { _, position ->
            block.invoke(list[position], position)
        }
    }

    open fun setData(orig: List<T>) {
        list.clear()
        list.addAll(orig)
        notifyDataSetChanged()
    }

    open fun addData(data: T) {
        if (data != null) {
            this.list.add(data)
            notifyItemInserted(list.size - 1)
        }
    }

    fun deleteData(index: Int) {
        this.list.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, this.list.size - index)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    abstract fun convert(holder: K, item: T?)

    override fun onBindViewHolder(holder: K, position: Int) {
        convert(holder, list[position])
    }

    override fun getItemCount(): Int = list.size
}