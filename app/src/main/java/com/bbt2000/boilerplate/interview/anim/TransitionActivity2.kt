package com.bbt2000.boilerplate.interview.anim

import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.transition.TransitionInflater
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R

class TransitionActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        setContentView(R.layout.activity_transition2)
        setupTransition()

        val btn = findViewById<Button>(R.id.button)
        btn.setOnClickListener {
            finishAfterTransition()
        }
    }

    private fun setupTransition() {
//        val explode = TransitionInflater.from(this).inflateTransition(R.transition.activity_explode)
        val slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide)
        val explode = Explode().setDuration(1000)
//        val slide = Slide().setDuration(1000)
        window.enterTransition = slide
        window.returnTransition = explode
    }
}