package com.bbt2000.boilerplate.demos.usb

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {

            }

            DisposableEffect(key1 = Unit) {
                HidDeviceUtil.registerReceiver()
                onDispose {
                    HidDeviceUtil.unregisterReceiver()
                    HidDeviceUtil.closeDevice()
                }
            }

            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                HidDeviceUtil.enumDevice(onDeviceOpened = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO) {
                            repeat(Int.MAX_VALUE) {
                                delay(100)
                                if (HidDeviceUtil.hidDevice == null) {
                                    coroutineScope.cancel()
                                }
                                val packetSize = HidDeviceUtil.packetSize ?: 16
                                val array = ByteArray(16)
                                val len = HidDeviceUtil.read(array, 16)
                                Logger.d("len = $len")
                                Logger.d("${array[0]}")
                            }
                        }
                    }
                })
            }
        }
    }
}



























































