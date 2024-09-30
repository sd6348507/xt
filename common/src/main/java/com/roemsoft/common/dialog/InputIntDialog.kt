package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx

class InputIntDialog(context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Input) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_input_int, null, false) }

    var title: String = ""
    var content: String = "1"
    var onConfirm: ((String) -> Boolean)? = null
    var onCancel: (() -> Unit)? = null
    var showCancel: Boolean = false

    fun build(): InputIntDialog {
        mDialog.apply {
            setContentView(mDialogView)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.CENTER
                    width = context.dpToPx(280)
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        mDialogView.findViewById<TextView>(R.id.dialog_input_title).text = title

        mDialogView.findViewById<EditText>(R.id.dialog_input_content).setText(content)

        mDialogView.findViewById<TextView>(R.id.dialog_input_cancel).visibility = if (showCancel) View.VISIBLE else View.GONE
        mDialogView.findViewById<TextView>(R.id.dialog_input_cancel).setOnClickListener {
            onCancel?.invoke()
            mDialog.dismiss()
        }
        mDialogView.findViewById<TextView>(R.id.dialog_input_confirm).setOnClickListener {
            val dismiss = onConfirm?.invoke(mDialogView.findViewById<EditText>(R.id.dialog_input_content).text.toString()) ?: false
            if (dismiss) {
                mDialog.dismiss()
            }
        }

        return this
    }

    fun show() {
        mDialog.show()
    }
}

fun AppCompatActivity.showInputIntDialog(creation: InputIntDialog.() -> Unit) {
    InputIntDialog(this).apply {
        creation()
    }.also { it.build().show() }
}