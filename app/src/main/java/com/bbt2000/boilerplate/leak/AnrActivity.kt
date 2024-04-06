package com.bbt2000.boilerplate.leak

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
                    Thread.sleep(10000)
                    Toast.makeText(this@AnrActivity, "Toast sth", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Click")
                }
            }
        }
    }
}














