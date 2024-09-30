package com.roemsoft.common

import android.os.Process

object CrashHandler : Thread.UncaughtExceptionHandler {

    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null

    fun init() {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread?, e: Throwable) {
        e.printStackTrace()
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler?.uncaughtException(t, e)
        } else {
            Process.killProcess(Process.myPid())
        }
    }
}