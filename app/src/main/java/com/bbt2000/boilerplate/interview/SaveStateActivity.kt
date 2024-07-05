package com.bbt2000.boilerplate.interview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orhanobut.logger.Logger

class SaveStateActivity : ComponentActivity() {
    private var savedText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedText = savedInstanceState?.getString("savedText") ?: ""
        Logger.d("savedText = $savedText")

        setContent {
            var text by remember { mutableStateOf(savedText) }
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text, onValueChange = {
                    text = it
                    savedText = text
                })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("savedText", savedText)
        Logger.d("savedText = $savedText")
    }
}