package com.bbt2000.boilerplate.demos.ffmpeg

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.viewinterop.AndroidView

/**
 *  author : suzhou
 *  date : 2023/10/31
 *  description :
 */
class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            AndroidView(factory = {
                SurfaceViewTest(it)
            })
        }
    }
}




















