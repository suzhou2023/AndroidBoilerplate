package com.bbt2000.boilerplate.demos.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R

class ViewComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vb)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, ViewComposeFragment())
            .commit()
    }
}