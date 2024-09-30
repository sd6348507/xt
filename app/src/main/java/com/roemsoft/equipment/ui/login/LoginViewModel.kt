package com.roemsoft.equipment.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.roemsoft.common.bean.doError
import com.roemsoft.common.bean.doFailure
import com.roemsoft.common.bean.doSuccess
import com.roemsoft.common.preference.Preference
import com.roemsoft.equipment.App
import com.roemsoft.equipment.BuildConfig
import com.roemsoft.equipment.ui.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class LoginViewModel : BaseViewModel() {

    private var user: String by Preference(Preference.USERNAME_KEY, "")

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    val username = MutableLiveData(if (BuildConfig.DEBUG) "PX007" else user)
    val password = MutableLiveData(if (BuildConfig.DEBUG)  "123456" else "")

    private val _login = MutableLiveData<Unit>()
    val login: LiveData<Unit> = _login

    fun login() {
    //    _login.value = null
    //    return

        if (!validate()) {
            return
        }

        viewModelScope.launch {
            repository.login(username.value.toString().trim(), password.value.toString().trim())
                .onStart { _loading.value = true }
                .catch { _loading.value = false }
                .onCompletion { _loading.value = false }
                .collectLatest { result ->
                    result.doSuccess {
                        user = username.value!!
                        App.userCode = it.name
                        _login.value = null
                    }
                    result.doFailure { showToast(it) }
                    result.doError { showToast(it) }
                }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (username.value.toString().trim().isEmpty()) {
            showToast("账号不能为空！")
            valid = false
        }
        /*if (password.value.isNullOrEmpty()) {
            showToast("密码不能为空！")
            valid = false
        }*/
        return valid
    }
}