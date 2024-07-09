package com.bbt2000.boilerplate.interview.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.orhanobut.logger.Logger

class SurvivalService : Service() {

    private val countBinder = CountBinder()

    class CountBinder : Binder() {
        fun getCount(): Long {
            return 0
        }
    }

    override fun onCreate() {
        super.onCreate()
        Logger.d("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("onDestroy")
    }

    override fun onBind(intent: Intent): IBinder {
        Logger.d("onBind")
        return countBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.d("onUnbind")
        return super.onUnbind(intent)
    }
}