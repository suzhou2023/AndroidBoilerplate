package com.bbt2000.boilerplate.demos.cupcake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.bbt2000.boilerplate.demos.cupcake.ui.theme.CupcakeTheme

// compose navigation demo
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CupcakeTheme { CupcakeApp() }
        }
    }
}
