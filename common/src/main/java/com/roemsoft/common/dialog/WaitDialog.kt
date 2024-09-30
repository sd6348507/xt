package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.roemsoft.common.R
import timber.log.Timber

class WaitDialog(context: Context) {
    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Wait) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_wait, null, false) }

 //   var title: String = "请等待"

    fun build(): WaitDialog {
        Timber.tag("dialog").i("======> build")

 //       mDialogView.findViewById<TextView>(R.id.title).text = title

        mDialog.apply {
            setContentView(mDialogView)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.CENTER
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        return this
    }

    fun isShowing() = mDialog.isShowing

    fun setTitle(title: String) {
        mDialogView.findViewById<TextView>(R.id.title).text = title
    }

    fun show() {
        mDialog.show()
    }

    fun show(title: String) {
        mDialogView.findViewById<TextView>(R.id.title).text = title
        mDialog.show()
    }

    fun hide() {
        mDialog.dismiss()
    }
}