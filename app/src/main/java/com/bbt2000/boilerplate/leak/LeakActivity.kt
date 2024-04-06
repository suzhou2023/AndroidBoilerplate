package com.bbt2000.boilerplate.leak

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference

class LeakActivity : ComponentActivity() {
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Logger.d("onReceive...")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text(text = "LeakActivity")
        }

        // 场景1：单例或静态变量持有Activity的引用
        //Leak1.activityLeak = this

        // 场景2：非静态内部类(匿名内部类)生命周期长于外部类
        //Leak1.innerClass = InnerClass()

        // 弱引用避免内存泄漏
//        Leak1.staticInnerClass = StaticInnerClass().apply {
//            activity = WeakReference(this@LeakActivity)
//        }

//        MyHandler().sendEmptyMessage(1)

        // 广播接收器造成的内存泄漏
        registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    // 场景2
    inner class InnerClass {
        // ...
    }

    // 弱引用避免内存泄漏
    class StaticInnerClass {
        var activity: WeakReference<Activity>? = null
    }

    // Handler造成的内存泄漏
    class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            Logger.d("handle message...")
            this.sendEmptyMessageDelayed(1, 1000)
        }
    }

    // 广播接收器造成的内存泄漏
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
    }
}






