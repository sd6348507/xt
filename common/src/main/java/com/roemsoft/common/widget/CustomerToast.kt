package com.roemsoft.common.widget

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx

class CustomerToast constructor(val context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {

    private var toast: Toast = Toast(context)

    init {
        val view = View.inflate(context, R.layout.toast_custom, null)
        view.findViewById<TextView>(R.id.tv_prompt).text = message
        toast.duration = duration
        toast.view = view
        toast.setGravity(Gravity.CENTER, 0, 0)
    }

    constructor(context: Context, resId: Int, duration: Int) : this(context, context.getString(resId), duration)


    fun show() {
        toast.show()
    }

    fun show(xOffset: Int = 0, yOffset: Int = 0) {
        toast.setGravity(Gravity.CENTER, xOffset, yOffset)
        toast.show()
    }

    fun showBottom() {
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    //    show(0, context.dpToPx(160))
    }
}