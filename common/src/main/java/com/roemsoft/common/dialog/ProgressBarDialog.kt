package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.roemsoft.common.R
import com.roemsoft.common.isScanKeyCode

class ProgressBarDialog(context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Alert) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_progressbar, null, false) }

    fun build(): ProgressBarDialog {
        mDialog.apply {
            setContentView(mDialogView)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.CENTER
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.MATCH_PARENT
                //    width = context.dpToPx(200)
                //    height = context.dpToPx(200)
                }
            }

            setOnKeyListener { _, keyCode, _ ->
                isScanKeyCode(keyCode)
            }
        }

        return this
    }

    fun show() {
        mDialog.show()
    }

    fun hide() {
        mDialog.dismiss()
    }
}