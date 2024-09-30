package com.roemsoft.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.IBinder
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun Context.dpToPx(dp: Int): Int {
    val scale = this.resources.displayMetrics.density
    return (scale * dp + 0.5).toInt()
}

fun Context.isNetworkConnected(): Boolean {
    val cm = applicationContext.getSystemService(ConnectivityManager::class.java)
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

fun Context.hideSoftKeyboard(windowToken: IBinder) {
    val im = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showSoftKeyboard() {
    val im = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun <T: LiveEvent> LifecycleOwner.eventObserve(eventType: Class<T>, block: (T) -> Unit) {
    LiveEventBus.get(eventType).observe(this, Observer {
        block(it)
    })
}

fun <T: LiveEvent> postEvent(eventType: T) {
    LiveEventBus.get(eventType.javaClass).post(eventType)
}

fun isScanKeyCode(keyCode: Int): Boolean {
    return true
//    return keyCode == ScannerConfig.ScanLeftKeyCode || keyCode == ScannerConfig.ScanRightKeyCode || keyCode == KeyEvent.KEYCODE_BUTTON_A || keyCode == 241
}

//判断是否要收起键盘
fun View.isShouldHideKeyboard(event: MotionEvent): Boolean {
    if (this is EditText) {
        val point = intArrayOf(0, 0)
        this.getLocationInWindow(point)
        val left = point[0]
        val top = point[1]
        val right = left + this.width
        val bottom = top + this.height
        return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
    }
    return true
}

fun today(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val calendar = Calendar.getInstance(Locale.CHINA)
    return format.format(calendar.time)
}

fun parseDate(dateStr: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return format.parse(dateStr) ?: Calendar.getInstance(Locale.CHINA).time
}

fun Calendar.format(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return format.format(this.time)
}

fun compareDate(firstDateStr: String, secondDateStr: String): Int {
    return parseDate(firstDateStr).time.compareTo(parseDate(secondDateStr).time)
}

fun EditText.notAllowNullOrEmpty() {
    this.addTextChangedListener(
        afterTextChanged = {
            if (it?.trim().toString().length > 1 && it?.trim().toString().startsWith("0")) {
                val qty = try {
                    it?.trim().toString().toInt()
                } catch (e: Exception) {
                    -1
                }
                val qtyStr = if (qty <= 0) {
                    "0"
                } else {
                    qty.toString()
                }
                this.setText(qtyStr)
                this.setSelection(qtyStr.length)
            }
        }
    )
}

fun EditText.floatNotAllowNullOrEmpty() {
    this.addTextChangedListener(
        afterTextChanged = {
            if (it?.trim().toString().length > 1 && it?.trim().toString().startsWith("0") && !it?.trim().toString().startsWith("0.")) {
                val qty = try {
                    it?.trim().toString().toInt()
                } catch (e: Exception) {
                    -1
                }
                val qtyStr = if (qty <= 0) {
                    "0"
                } else {
                    qty.toString()
                }
                this.setText(qtyStr)
                this.setSelection(qtyStr.length)
            }
        }
    )
}

fun View.onSingleClick(listener: (View) -> Unit) {
    setOnClickListener {
        val millis = this.getTag(R.id.single_click_tag_last_click) as? Long ?: 0
        if (SystemClock.uptimeMillis() - millis >= 500) {
            this.setTag(
                R.id.single_click_tag_last_click, SystemClock.uptimeMillis()
            )
            listener.invoke(this)
        }
    }
}

fun AppCompatActivity.onBackPressed(isEnable: Boolean, callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(isEnable) {
        override fun handleOnBackPressed() {
            callback()
        }
    })
}