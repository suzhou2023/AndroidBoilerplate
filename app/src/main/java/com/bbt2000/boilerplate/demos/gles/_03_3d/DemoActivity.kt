package com.bbt2000.boilerplate.demos.gles._03_3d

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.viewinterop.AndroidView
import com.bbt2000.boilerplate.common.util.StatusBarUtil


/**
 *  author : sz
 *  date : 2023/11/21
 *  description :
 */
class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.transparentStatusBar(window)
        StatusBarUtil.transparentNavigationBar(window)
        StatusBarUtil.setStatusBarTextColor(window, light = false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            AndroidView(factory = {
                SurfaceViewTest(it).apply { addCallback() }
            })
        }
    }
}





































