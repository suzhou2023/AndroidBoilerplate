package com.bbt2000.boilerplate.leak

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LeakActivity : ComponentActivity() {
    val handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 场景1：单例或静态变量持有Activity的引用
        // Leak1.activityLeak = this
        setContent {
            Text(text = "LeakActivity")
        }

        val runnable = Runnable {
            CoroutineScope(Dispatchers.Default).launch {
                repeat(10) {
                    delay(1000)
                    Logger.d("Runnable task...")
                }
            }
        }

        handler.post(runnable)
    }


}







