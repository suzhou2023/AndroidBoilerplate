package com.bbt2000.boilerplate

import android.app.Application
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 *  author : suzhou
 *  date : 2023/8/23
 *  description :
 */
class BpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextUtil.application = this
        asyncInit()
    }
}


fun asyncInit() {
    Logger.addLogAdapter(AndroidLogAdapter())
}