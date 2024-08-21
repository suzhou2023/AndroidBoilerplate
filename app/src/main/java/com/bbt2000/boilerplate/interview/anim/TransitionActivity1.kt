package com.bbt2000.boilerplate.interview.anim

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.bbt2000.boilerplate.R

class TransitionActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition1)

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            val intent = Intent(this, TransitionActivity2::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
        }
    }
}