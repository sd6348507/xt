package com.roemsoft.equipment.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.roemsoft.common.BaseApp
import com.roemsoft.common.isNetworkConnected
import com.roemsoft.common.onBackPressed
import com.roemsoft.equipment.R
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var mTipView: View
    private lateinit var mWindowManager: WindowManager
    private lateinit var mLayoutParams: WindowManager.LayoutParams

    private var mNetworkChangeBroadcast: BroadcastReceiver? = null
    private val mFilter: IntentFilter by lazy {
        IntentFilter().apply { addAction(ConnectivityManager.CONNECTIVITY_ACTION) }
    }
    private var mNetworkService: ConnectivityManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingView()

        initToolbar()

        initTipView()

   //     initColor()

        setupEvent()

        initView()

        start()

        if (mNetworkService == null) {
            mNetworkService = getSystemService(ConnectivityManager::class.java)
        }
        mNetworkService?.registerDefaultNetworkCallback(mNetworkCallback)

        onBackPressed(true) {
            val count = supportFragmentManager.backStackEntryCount
            if (count == 0) {
                finish()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        /*if (mNetworkChangeBroadcast == null) {
            mNetworkChangeBroadcast = NetworkChangeReceiver()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mNetworkChangeBroadcast, mFilter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(mNetworkChangeBroadcast, mFilter)
        }*/
        super.onResume()
    }

    override fun onPause() {
        /*if (mNetworkChangeBroadcast != null) {
            unregisterReceiver(mNetworkChangeBroadcast)
        }
        mNetworkChangeBroadcast = null*/
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /*override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        mNetworkService?.unregisterNetworkCallback(mNetworkCallback)
    }

    override fun finish() {
        super.finish()
        if (mTipView.parent != null) {
            mWindowManager.removeView(mTipView)
        }
    }

    abstract fun bindingView()

    abstract fun initView()

    open fun initToolbar() {
        val toolbar = getToolbar()

        toolbar?.run {
            setSupportActionBar(this)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        setToolTitle()
    }

    abstract fun getToolbar(): Toolbar?

    abstract fun setToolTitle()

    open fun setupEvent() {
        /*eventObserve(NetworkEvent::class.java) {
            onNetworkChanged(it.isConnected)
        }*/
    }

    open fun doReconnected() {
        start()
    }

    open fun start() {}

    open fun enableNetworkTip(): Boolean = true

    private fun onNetworkChanged(hasNetwork: Boolean) {
        BaseApp.hasNetwork = hasNetwork
        if (enableNetworkTip()) {
            if (hasNetwork) {
                doReconnected()
                if (mTipView.parent != null) {
                    mWindowManager.removeView(mTipView)
                }
            } else {
                if (mTipView.parent == null) {
                    mWindowManager.addView(mTipView, mLayoutParams)
                }
            }
        }
    }

    private fun initTipView() {
        mTipView = layoutInflater.inflate(R.layout.layout_network_tip, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mLayoutParams = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        mLayoutParams.x = 0
        mLayoutParams.y = 0
        mLayoutParams.gravity = Gravity.TOP
        mLayoutParams.windowAnimations = R.style.anim_float_view
    }

    /*private fun initColor() {
        setTransparentStatus()
    }*/

    /*private fun setTransparentStatus() {
        window.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = resources.getColor(R.color.colorPrimary)
        }
    }*/

    private val mNetworkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            onNetworkChanged(true)
            Timber.e("==网络连接成功，通知可以使用的时候调用====onAvailable===")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            onNetworkChanged(false)

            Timber.e("==当网络已断开连接时调用===onLost===");
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (isNetworkConnected()) {
                Timber.d("onCapabilitiesChanged ---> ====网络可正常上网")
            }

            //表明此网络连接验证成功
            if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Timber.d("===当前在使用Mobile流量上网===")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Timber.d("====当前在使用WiFi上网===")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                    Timber.d("=====当前使用蓝牙上网=====")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Timber.d("=====当前使用以太网上网=====")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    Timber.d("===当前使用VPN上网====")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)) {
                    Timber.d("===表示此网络使用Wi-Fi感知传输====")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN)) {
                    Timber.d("=====表示此网络使用LoWPAN传输=====")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_USB)) {
                    Timber.d("=====表示此网络使用USB传输=====")
                }
            }
        }
    }
}