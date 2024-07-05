package com.bbt2000.boilerplate.interview.lifecycle

import android.content.Intent
import android.content.res.Configuration
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
 *
 * 横竖屏切换时，Activity会重建，所以会重新调用Activity生命周期方法。但是如果配置了：
 * android:configChanges="orientation|screenSize"，则Activity不会重建，而是会调用
 * onConfigurationChanged方法。
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Logger.d("onConfigurationChanged")
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