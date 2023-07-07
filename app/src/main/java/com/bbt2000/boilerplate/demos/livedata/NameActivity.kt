package com.bbt2000.boilerplate.demos.livedata

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bbt2000.boilerplate.R

class NameActivity : AppCompatActivity() {
    // ?
    private val model: NameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_name)
        val textView = findViewById<TextView>(R.id.textView)

        // Create the observer which updates the UI.
        val nameObserver = Observer<String> { newName ->
            textView.text = newName
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        model.currentName.observe(this, nameObserver)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            model.currentName.setValue("John Doe")
        }
    }
}