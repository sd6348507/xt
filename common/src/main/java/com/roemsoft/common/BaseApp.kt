package com.roemsoft.common

import android.app.Application
import android.content.Context
import com.jeremyliao.liveeventbus.LiveEventBus
import com.roemsoft.common.preference.Preference
import com.roemsoft.common.api.HttpConfig
import com.roemsoft.common.bean.User
import timber.log.Timber
import kotlin.properties.Delegates

open class BaseApp : Application() {
    companion object {
        var hasNetwork: Boolean = true

        var baseUrl: String by Preference(Preference.URL_KEY, HttpConfig.BASE_URL)

        var context: Context by Delegates.notNull()
            private set
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        Timber.plant(Timber.DebugTree())

        LiveEventBus.config().lifecycleObserverAlwaysActive(true)

        CrashHandler.init()
    }
}