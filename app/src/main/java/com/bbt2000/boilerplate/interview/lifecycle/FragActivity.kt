package com.bbt2000.boilerplate.interview.lifecycle

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R
import com.orhanobut.logger.Logger

/**
 * 静态Fragment与Activity的生命周期：
 * 启动：
 * Activity.onCreate -> Fragment.onAttach -> Fragment.onCreate -> Fragment.onCreateView ->
 * Fragment.onViewCreated -> Fragment.onStart -> Activity.onStart -> Activity.onResume ->
 * Fragment.onResume
 * 退出：
 * Fragment.onPause -> Activity.onPause -> Fragment.onStop -> Activity.onStop ->
 * Fragment.onDestroyView -> Fragment.onDestroy -> Fragment.onDetach -> Activity.onDestroy
 * 后台到前台：
 * Activity.onRestart -> Fragment.onStart -> Activity.onStart -> Activity.onResume ->
 * Fragment.onResume
 */
class FragActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("onCreate")
        setContentView(R.layout.activity_frag)
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TestFragment())
                .commitNow()
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