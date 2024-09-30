package com.roemsoft.equipment.ui.login

import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.dialog.ProgressBarDialog
import com.roemsoft.common.hideSoftKeyboard
import com.roemsoft.equipment.R
import com.roemsoft.equipment.databinding.ActivityLoginBinding
import com.roemsoft.equipment.ui.DataBindingAppCompatActivity
import com.roemsoft.equipment.ui.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class LoginActivity : DataBindingAppCompatActivity() {

    private val binding by binding<ActivityLoginBinding>(R.layout.activity_login)

    override val viewModel: LoginViewModel by viewModels()

    private val dialog by lazy {
        ProgressBarDialog(this).build()
    }

    override fun bindingView() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    override fun initToolbar() {  }

    override fun getToolbar(): Toolbar? = null

    override fun setToolTitle() { }

    override fun initView() {
        val width = resources.displayMetrics.widthPixels
        binding.loginBg.post {
            binding.loginBg.layoutParams.width = width
            binding.loginBg.layoutParams.height = (width * 0.81f).toInt()
        }
    }

    override fun setupEvent() {
        super.setupEvent()

        viewModel.loading.observe(this) {
            if (it) {
                dialog.show()
            } else {
                dialog.hide()
            }
        }

        viewModel.login.observe(this) {
            gotoMain()
            finish()
        }
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //如果是点击事件，获取点击的view，并判断是否要收起键盘
        if (ev.action == MotionEvent.ACTION_DOWN) {
            //获取目前得到焦点的view
            val v = currentFocus
            //判断是否要收起并进行处理
            if (v != null && isShouldHideKeyboard(v, ev)) {
                this.hideSoftKeyboard(v.windowToken)
                v.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //判断是否要收起键盘
    private fun isShouldHideKeyboard(v: View, event: MotionEvent): Boolean {
        //如果目前得到焦点的这个view是editText的话进行判断点击的位置
        if (v is EditText) {
            val point = intArrayOf(0, 0)
            v.getLocationInWindow(point)
            val left = point[0]
            val top = point[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            // 点击EditText的事件，忽略它。
            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上
        return false
    }
}