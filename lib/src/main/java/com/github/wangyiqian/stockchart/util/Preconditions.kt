package com.github.wangyiqian.stockchart.util

import android.os.Looper

fun checkMainThread(errMsg: String = "Cannot invoke this method on a background thread") {
    if (Thread.currentThread() != Looper.getMainLooper().thread) {
        throw IllegalStateException(errMsg)
    }
}