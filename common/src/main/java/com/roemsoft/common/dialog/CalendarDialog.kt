package com.roemsoft.common.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.roemsoft.common.R
import com.roemsoft.common.dpToPx
import com.roemsoft.common.format
import com.roemsoft.common.parseDate
import java.util.*

class CalendarDialog(context: Context) {

    private val mDialog by lazy { Dialog(context, R.style.Theme_App_Dialog_Alert) }
    private val mDialogView by lazy { LayoutInflater.from(context).inflate(R.layout.dialog_calendar, null, false) }

    var title: String = ""
    var dateStr: String = ""
    var onConfirm: ((String) -> Unit)? = null

    var selectCalendar: Calendar = Calendar.getInstance(Locale.CHINA)

    fun build(): CalendarDialog {
        mDialog.apply {
            setContentView(mDialogView)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            window?.let {
                it.attributes.apply {
                    gravity = Gravity.CENTER
                //    width = context.dpToPx(480)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
        }

        mDialogView.findViewById<TextView>(R.id.dialog_calendar_title).text = title
        mDialogView.findViewById<CalendarView>(R.id.dialog_calendar).date = parseDate(dateStr).time

        mDialogView.findViewById<CalendarView>(R.id.dialog_calendar).setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectCalendar.set(year, month, dayOfMonth)
        }

        mDialogView.findViewById<TextView>(R.id.dialog_calendar_cancel).setOnClickListener {
            mDialog.dismiss()
        }
        mDialogView.findViewById<TextView>(R.id.dialog_calendar_confirm).setOnClickListener {
            onConfirm?.invoke(selectCalendar.format())
            mDialog.dismiss()
        }

        return this
    }

    fun show() {
        mDialog.show()
    }
}

fun AppCompatActivity.showCalendarDialog(creation: CalendarDialog.() -> Unit) {
    CalendarDialog(this).apply {
        creation()
    }.also { it.build().show() }
}