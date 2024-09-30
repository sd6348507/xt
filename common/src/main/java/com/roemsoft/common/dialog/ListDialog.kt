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

class ListDialog(val context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_NoBackground) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_list, null, false) }

    private val adapter: DialogListAdapter by lazy {
        DialogListAdapter().apply {
            onItemClick = { _, index ->
                if (allowClear && index == 0) {
                    onConfirm?.invoke("")
                } else {
                    onConfirm?.invoke(this.list[index])
                }
                mDialog.dismiss()
            }
        }
    }

    var title = ""
    var content = listOf<String>()
    var onConfirm: ((String) -> Unit)? = null
    var allowClear: Boolean = false

    fun build(): ListDialog {
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
            layoutManager = LinearLayoutManager(this@ListDialog.context)
            addItemDecoration(SpaceItemDecoration(this@ListDialog.context))
            adapter = this@ListDialog.adapter
        }
        adapter.setData(content)

        if (allowClear) {
            adapter.list.add(0, "清空")
        }

        mDialogView.findViewById<TextView>(R.id.dialog_list_title).text = title

        return this
    }

    fun show() {
        mDialog.show()
    }
}

class DialogListAdapter : BaseListAdapter<String, DialogListAdapter.DialogListViewHolder>() {

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

fun AppCompatActivity.showListDialog(creation: ListDialog.() -> Unit) {
    ListDialog(this).apply {
        creation()
    }.also { it.build().show() }
}