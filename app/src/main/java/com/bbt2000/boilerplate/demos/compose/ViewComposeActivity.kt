package com.bbt2000.boilerplate.demos.compose

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.bbt2000.boilerplate.R

class ViewComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_view_compose)
        findViewById<ComposeView?>(R.id.composeView).setContent {
            MessageCard(
                msg = Message(
                    "Colleague",
                    "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
                ),
            )
        }
    }
}


