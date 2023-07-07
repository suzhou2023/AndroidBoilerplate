package com.bbt2000.boilerplate.demos.viewbinding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R


// fragment viewbinding demo
class ViewBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vb)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, ViewBindingFragment())
            .commit()
    }
}
