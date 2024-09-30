package com.roemsoft.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.roemsoft.common.BaseApp
import com.roemsoft.common.bus.NetworkEvent
import com.roemsoft.common.isNetworkConnected
import com.roemsoft.common.postEvent

class NetworkChangeReceiver : BroadcastReceiver() {

    private val event = NetworkEvent(BaseApp.hasNetwork)

    override fun onReceive(context: Context, intent: Intent?) {
        val isConnected = context.isNetworkConnected()
        if (!isConnected || isConnected != BaseApp.hasNetwork) {
            postEvent(event.apply { this.isConnected = isConnected })
        }
    }
}