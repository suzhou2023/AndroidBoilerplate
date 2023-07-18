package com.bbt2000.boilerplate.demos.gles._03_texture

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


/**
 *  author : sz
 *  date : 2023/7/13 14:54
 *  description :
 */

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(SurfaceViewTest(this))
    }
}
