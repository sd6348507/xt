package com.roemsoft.equipment

import android.content.Context
import android.os.Build
import com.jeremyliao.liveeventbus.LiveEventBus
import com.roemsoft.common.BaseApp
import timber.log.Timber
import kotlin.properties.Delegates

class App : BaseApp() {

    companion object {
        var userCode: String? = null    // 登录工号

        var context: Context by Delegates.notNull()
            private set

        fun isPda(): Boolean {
            Timber.tag("device name").i("device : ${Build.DEVICE}")
            return Build.DEVICE.startsWith("n5", true)
        }
    }

    override fun onCreate() {
        super.onCreate()

        LiveEventBus.config().lifecycleObserverAlwaysActive(true)
    }
}