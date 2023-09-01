package com.bbt2000.boilerplate

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


/**
 *  author : sz
 *  date : 2023/8/21
 *  description :
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.addLogAdapter(AndroidLogAdapter())
    }
}

















