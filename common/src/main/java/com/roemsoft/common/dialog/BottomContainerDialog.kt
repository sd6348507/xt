package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx

class BottomContainerDialog(val context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Alert) }

    var view: (() -> View)? = null
        set(value) {
            field = value
            value?.let {
                mDialog.setContentView(it.invoke())
            }
        }

    var onShow: (() -> Unit)? = null
    var onDismiss: (() -> Unit)? = null

    fun build(): BottomContainerDialog {
        mDialog.apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)

            setOnShowListener { onShow?.invoke() }
            setOnDismissListener { onDismiss?.invoke() }

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.BOTTOM
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        return this
    }

    fun show() {
        mDialog.show()
    }

    fun dismiss() {
        mDialog.dismiss()
    }

    fun isShowing() = mDialog.isShowing
}

fun AppCompatActivity.bottomContainerDialog(creation: BottomContainerDialog.() -> Unit): BottomContainerDialog {
    return BottomContainerDialog(this).apply {
        creation()
    }.also { it.build() }
}