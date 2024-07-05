package com.bbt2000.boilerplate.interview.lifecycle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R
import com.orhanobut.logger.Logger

/**
 * 一个Activity启动另一个Activity生命周期：
 * FirstActivity.onPause -> SecondActivity.onCreate -> SecondActivity.onStart ->
 * SecondActivity.onResume -> FirstActivity.onStop
 *
 * 从另一个Activity回退到前一个Activity：
 * SecondActivity.onPause -> FirstActivity.onRestart -> FirstActivity.onStart ->
 * FirstActivity.onResume -> SecondActivity.onStop -> SecondActivity.onDestroy
 */
class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("onCreate")
        setContentView(R.layout.activity_first)
    }

    override fun onRestart() {
        super.onRestart()
        Logger.d("onRestart")
    }

    override fun onStart() {
        super.onStart()
        Logger.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Logger.d("onResume")
        findViewById<Button>(R.id.button).setOnClickListener {
            var intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        Logger.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("onDestroy")
    }
}