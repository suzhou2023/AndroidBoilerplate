package com.bbt2000.boilerplate.interview

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text

class AnrActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text(text = "AnrActivity")

                Button(onClick = {
                    // 模拟器会弹出无响应窗口，华为手机不会
                    // 看logcat会提示到具体的Activity，主线程有耗时操作
                    // 也可以利用命令导出系统bugreport: adb bugreport > bugreport.txt，搜索ANR关键字也可定位到Activity
                    Thread.sleep(10000)
                    Toast.makeText(this@AnrActivity, "Toast sth", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Click")
                }
            }
        }
    }
}














