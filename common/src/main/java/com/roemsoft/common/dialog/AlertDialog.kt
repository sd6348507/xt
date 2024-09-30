package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx
import com.roemsoft.common.isScanKeyCode

class AlertDialog(context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Alert) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_alert, null, false) }

    var content: String = ""
    var onConfirm: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    fun build(): AlertDialog {
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

            setOnKeyListener { _, keyCode, _ ->
                isScanKeyCode(keyCode)
            }
        }

        mDialogView.findViewById<TextView>(R.id.dialog_alert_content).text = content
        mDialogView.findViewById<TextView>(R.id.dialog_alert_cancel).setOnClickListener {
            onCancel?.invoke()
            mDialog.dismiss()
        }
        mDialogView.findViewById<TextView>(R.id.dialog_alert_confirm).setOnClickListener {
            onConfirm?.invoke()
            mDialog.dismiss()
        }

        return this
    }

    fun show() {
        mDialog.show()
    }
}

fun AppCompatActivity.showAlertDialog(creation: AlertDialog.() -> Unit) {
    AlertDialog(this).apply {
        creation()
    }.also { it.build().show() }
}