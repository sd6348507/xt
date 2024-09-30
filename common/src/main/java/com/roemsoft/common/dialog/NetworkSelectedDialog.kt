package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roemsoft.common.BaseApp
import com.roemsoft.common.R
import com.roemsoft.common.api.HttpConfig
import com.roemsoft.common.dpToPx

class NetworkSelectedDialog(val context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_NoBackground) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_select, null, false) }

    var content = arrayListOf(HttpConfig.NetUrl.MOBILE, HttpConfig.NetUrl.WIFI)
    var onSelected: ((HttpConfig.NetUrl) -> Unit)? = null

    fun build(): NetworkSelectedDialog {
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

        val container = mDialogView.findViewById<LinearLayout>(R.id.dialog_select_container)

        for (data in content) {
            val v = LayoutInflater.from(context).inflate(R.layout.item_dialog_network_selected, container, false) as TextView
            v.apply {
                text = data.title
                isSelected = data.url == BaseApp.baseUrl
                setOnClickListener {
                    onSelected?.invoke(data)
                    BaseApp.baseUrl = data.url
                    mDialog.dismiss()
                }
            }
            container.addView(v)
        }

        return this
    }

    fun show() {
        mDialog.show()
    }
}

fun AppCompatActivity.showNetworkSelectedDialog(creation: NetworkSelectedDialog.() -> Unit) {
    NetworkSelectedDialog(this).apply {
        creation()
    }.also { it.build().show() }
}