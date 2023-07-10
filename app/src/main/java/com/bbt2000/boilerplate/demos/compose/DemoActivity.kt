package com.bbt2000.boilerplate.demos.compose

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbt2000.boilerplate.demos.compose.animation.AnimatableDemo
import com.bbt2000.boilerplate.demos.compose.animation.SwitchBlock
import com.bbt2000.boilerplate.demos.compose.paint.MyPaint10
import com.bbt2000.boilerplate.demos.compose.paint.RecordButton
import com.bbt2000.boilerplate.demos.compose.paint.SwitchButton

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                RecordButton(
                    widthDp = 67.dp,
                    totalSeconds = 10,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) { recordState ->
                    println(recordState)
                }
            }
        }
    }
}







