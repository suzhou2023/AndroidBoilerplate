package com.bbt2000.boilerplate.demos.gles._01_basic

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.permissionx.guolindev.PermissionX


/**
 *  author : sz
 *  date : 2023/7/13 14:54
 *  description :
 */

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        transparentStatusBar(window)
        transparentNavBar(window)

        setContentView(SurfaceViewTest(this))

        setContent {
            val showSurfaceView = remember { mutableStateOf(true) }

            Box(modifier = Modifier.fillMaxSize()) {
                if (showSurfaceView.value) {
                    AndroidView(
                        factory = {
                            SurfaceViewTest(it)
                        }
                    )
                }

                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp),
                    onClick = {
                        PermissionX.init(this@DemoActivity)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    showSurfaceView.value = true
                                }
                            }
                    }
                ) {
                    Text("Click")
                }
            }
        }
    }

    private fun transparentStatusBar(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val vis = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = option or vis
        window.statusBarColor = Color.TRANSPARENT
    }

    private fun transparentNavBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        window.navigationBarColor = Color.TRANSPARENT
        val decorView = window.decorView
        val vis = decorView.systemUiVisibility
        val option =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = vis or option
    }
}
