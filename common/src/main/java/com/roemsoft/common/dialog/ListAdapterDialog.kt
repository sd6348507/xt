package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roemsoft.common.R
import com.roemsoft.common.adapter.BaseListAdapter
import com.roemsoft.common.adapter.BaseViewHolder
import com.roemsoft.common.adapter.OnItemClickListener
import com.roemsoft.common.databinding.ItemDialogList1Binding
import com.roemsoft.common.dpToPx
import com.roemsoft.common.widget.SpaceItemDecoration

class ListAdapterDialog<T>(val context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_NoBackground) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_list, null, false) }

    private val adapter: DialogListAdapter2 by lazy {
        DialogListAdapter2().apply {
            onItemClick = { _, index ->
                onConfirm?.invoke(content[index])
                mDialog.dismiss()
            }
        }
    }

    var title = ""
    var content = listOf<T>()
    var map: ((T) -> String)? = null
    var onConfirm: ((T) -> Unit)? = null

    fun build(): ListAdapterDialog<T> {
        mDialog.apply {
            setContentView(mDialogView)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.CENTER
                    width = context.dpToPx(280)
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        mDialogView.findViewById<RecyclerView>(R.id.dialog_list_container).run {
            layoutManager = LinearLayoutManager(this@ListAdapterDialog.context)
            addItemDecoration(SpaceItemDecoration(this@ListAdapterDialog.context))
            adapter = this@ListAdapterDialog.adapter
        }

        map?.let {
            adapter.setData(content.map { item -> it.invoke(item) })
        }

        mDialogView.findViewById<TextView>(R.id.dialog_list_title).text = title

        return this
    }

    fun show() {
        mDialog.show()
    }

    fun hide() {
        if (mDialog.isShowing) {
            mDialog.hide()
        }
    }
}

class DialogListAdapter2 : BaseListAdapter<String, DialogListAdapter2.DialogListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogListViewHolder {
        return DialogListViewHolder.from(parent)
    }

    override fun convert(holder: DialogListViewHolder, item: String?) {
        item ?: return
        holder.bind(item, onItemClick)
    }

    class DialogListViewHolder private constructor(private val binding: ItemDialogList1Binding) : BaseViewHolder(binding.root) {

        fun bind(item: String, listener: OnItemClickListener?) {
            binding.data = item
            bindOnClickListener(listener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): DialogListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDialogList1Binding.inflate(layoutInflater, parent, false)

                return DialogListViewHolder(binding)
            }
        }
    }
}

fun <T> AppCompatActivity.showListAdapterDialog(creation: ListAdapterDialog<T>.() -> Unit): ListAdapterDialog<T> {
    return ListAdapterDialog<T>(this).apply {
        creation()
    }.also { it.build().show() }
}