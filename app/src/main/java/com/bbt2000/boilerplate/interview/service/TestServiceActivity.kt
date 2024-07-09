package com.bbt2000.boilerplate.interview.service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R
import com.orhanobut.logger.Logger

class TestServiceActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnBind: Button
    private lateinit var btnUnbind: Button

    lateinit var countBinder: SurvivalService.CountBinder
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            countBinder = service as SurvivalService.CountBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Logger.d("onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_service)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        btnBind = findViewById(R.id.btn_bind)
        btnUnbind = findViewById(R.id.btn_unbind)

        btnStart.setOnClickListener {
            val intent = Intent(this, SurvivalService::class.java)
            startService(intent)
        }
        btnStop.setOnClickListener {
            val intent = Intent(this, SurvivalService::class.java)
            stopService(intent)
        }
        btnBind.setOnClickListener {
            val intent = Intent(this, SurvivalService::class.java)
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
        btnUnbind.setOnClickListener {
            unbindService(connection)
        }
    }
}