package com.bbt2000.boilerplate.demos.compose

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.demos.compose.composable.imageClickable

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            Image(
                painter = painterResource(id = R.mipmap.ic_camera),
                contentDescription = null,
                modifier = Modifier
                    .size(67.dp)
                    .imageClickable {
                        Log.d("DemoActivity", "imageClickable")
                    }
            )
        }
    }
}







