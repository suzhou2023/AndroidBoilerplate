package com.bbt2000.boilerplate.demos.databinding

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bbt2000.boilerplate.R
import com.bbt2000.boilerplate.databinding.ActivityDbBinding

class DataBindingActivity : AppCompatActivity() {
    lateinit var binding: ActivityDbBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_db)
        binding.dataBindingActivity = this
    }

    fun buttonClick(v: View) {
        Toast.makeText(this, "button click", Toast.LENGTH_SHORT).show()
    }
}