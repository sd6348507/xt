package com.roemsoft.common.bus

import com.jeremyliao.liveeventbus.core.LiveEvent

class NetworkEvent(var isConnected: Boolean) : LiveEvent

class CollectEvent(var collectJson: String) : LiveEvent