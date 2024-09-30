package com.roemsoft.common.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindOnClickListener(listener: OnItemClickListener?) {
        listener ?: return
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.invoke(itemView, position)
            }
        }
    }

    open fun bindOnChildClickListener(view: View, listener: OnItemChildClickListener?) {
        if (!view.isClickable) {
            view.isClickable = true
        }

        listener?.let {
            view.setOnClickListener { v ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    it.invoke(v, position)
                }
            }
        }
    }

    open fun bindOnLongClickListener(view: View, listener: OnItemLongClickListener?) {
        if (!view.isClickable) {
            view.isClickable = true
        }

        listener?.let {
            view.setOnLongClickListener { v ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    it.invoke(v, position)
                    true
                } else {
                    false
                }
            }
        }
    }
}