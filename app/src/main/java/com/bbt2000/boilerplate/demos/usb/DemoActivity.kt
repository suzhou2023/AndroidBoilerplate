package com.bbt2000.boilerplate.demos.usb

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  author : suzhou
 *  date : 2023/10/31
 *  description :
 */
class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            var deviceOpened by remember { mutableStateOf(false) }

            DisposableEffect(key1 = Unit) {
                HidDeviceUtil.registerReceiver()
                HidDeviceUtil.enumDevice { deviceOpened = it }

                onDispose {
                    HidDeviceUtil.unregisterReceiver()
                }
            }

            LaunchedEffect(key1 = deviceOpened) {
                withContext(Dispatchers.IO) {
                    while (deviceOpened) {
                        delay(100)
                        Logger.d("hidReadTest...")
                        HidDeviceUtil.hidReadTest(object : HidDeviceUtil.KeyCallback {
                            override fun onKey(value: Int) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(this@DemoActivity, value.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hid按键测试")
                val state = if (deviceOpened) "打开" else "关闭"
                Text(text = "设备状态：$state")
            }
        }
    }
}



























































