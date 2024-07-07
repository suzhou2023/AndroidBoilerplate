package com.bbt2000.boilerplate.interview.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R

class PropertyAnimActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_anim)
    }

    override fun onResume() {
        super.onResume()
        val iv = findViewById<ImageView>(R.id.iv)
        val scaleX = ObjectAnimator.ofFloat(iv, "scaleX", 1f, 0.5f)
            .setDuration(2000)
        val scaleY = ObjectAnimator.ofFloat(iv, "scaleY", 1f, 0.5f)
            .setDuration(2000)
        val rotX = ObjectAnimator.ofFloat(iv, "rotationX", 0f, 360f)
            .setDuration(3000)
        val rotY = ObjectAnimator.ofFloat(iv, "rotationY", 0f, 360f)
            .setDuration(3000)

        val set = AnimatorSet()
        set.play(scaleX).with(scaleY).with(rotX).with(rotY)
        set.start()
    }
}