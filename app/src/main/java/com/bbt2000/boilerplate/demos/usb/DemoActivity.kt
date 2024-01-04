package com.bbt2000.boilerplate.demos.usb

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orhanobut.logger.Logger
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

        requireCameraPermission()

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
                if (deviceOpened) {
                    withContext(Dispatchers.IO) {
                        repeat(Int.MAX_VALUE) {
                            delay(1000)
                            Logger.d("hidReadTest...")
                            HidDeviceUtil.hidReadTest()
                        }
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

    private fun requireCameraPermission() {
        if (PermissionX.isGranted(this, Manifest.permission.CAMERA)) {

        } else {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.CAMERA)
                .request { allGranted, _, _ ->
                    if (allGranted) {

                    }
                }
        }
    }
}



























































