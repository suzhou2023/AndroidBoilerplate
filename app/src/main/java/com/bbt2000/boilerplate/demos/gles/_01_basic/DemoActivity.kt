package com.bbt2000.boilerplate.demos.gles._01_basic

import android.Manifest
import android.os.Bundle
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

        setContent {
            val showSurfaceView = remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()) {
                if (showSurfaceView.value) {
                    AndroidView(
                        factory = {
                            SurfaceViewTest(it).apply { addCallback() }
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
}





































