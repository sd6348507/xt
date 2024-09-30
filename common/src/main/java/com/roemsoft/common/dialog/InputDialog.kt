package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx

class InputDialog(val context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Alert) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_input, null, false) }

    var title: String = ""
    var onConfirm: ((String) -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    fun build(): InputDialog {
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

        (mDialogView.layoutParams as FrameLayout.LayoutParams).leftMargin = 16

        mDialogView.findViewById<TextView>(R.id.dialog_input_title).text = title

        mDialogView.findViewById<TextView>(R.id.dialog_input_cancel).setOnClickListener {
            onCancel?.invoke()
            mDialog.dismiss()
        }
        mDialogView.findViewById<TextView>(R.id.dialog_input_confirm).setOnClickListener {
            val number = mDialogView.findViewById<EditText>(R.id.dialog_input_content).text.toString().trim()
            onConfirm?.invoke(number)
            mDialog.dismiss()
        }

        return this
    }

    fun show() {
        mDialog.show()
    }

}

fun AppCompatActivity.showInputDialog(creation: InputDialog.() -> Unit) {
    InputDialog(this).apply {
        creation()
    }.also { it.build().show() }
}