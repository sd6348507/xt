package com.roemsoft.equipment.ui

import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.roemsoft.common.widget.CustomerToast
import com.roemsoft.equipment.R

abstract class DataBindingAppCompatActivity : BaseActivity() {

    open val viewModel: BaseViewModel by viewModels()

    protected inline fun <reified T: ViewDataBinding> binding(@LayoutRes resId: Int): Lazy<T> = lazy {
        DataBindingUtil.setContentView<T>(this, resId)
    }

    override fun setupEvent() {
        super.setupEvent()

        viewModel.toastResId.observe(this) {
            CustomerToast(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.toastText.observe(this) {
            CustomerToast(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.snackbarText.observe(this) { pair ->
            Snackbar.make(getToolbar() ?: window.decorView, pair.first, Snackbar.LENGTH_LONG).setAction(R.string.label_action_retry) {
                pair.second()
            }.show()
        }
    }
}