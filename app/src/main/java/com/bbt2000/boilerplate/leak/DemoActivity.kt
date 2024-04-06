package com.bbt2000.boilerplate.leak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class DemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(AndroidLogAdapter())

        setContent {
            Column {
                Button(onClick = {
                    startActivity(Intent(this@DemoActivity, LeakActivity::class.java))
                }) {
                    Text(text = "LeakActivity")
                }

                Button(onClick = {
                    startActivity(Intent(this@DemoActivity, AnrActivity::class.java))
                }) {
                    Text(text = "AnrActivity")
                }
            }
        }
    }
}







