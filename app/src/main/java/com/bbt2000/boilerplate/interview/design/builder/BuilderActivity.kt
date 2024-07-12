package com.bbt2000.boilerplate.interview.design.builder

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bbt2000.boilerplate.R

class BuilderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_builder)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setMessage("Hello AlertDialog!")
                .setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
                }
                .setPositiveButton("Okay") { _, _ ->
                    Toast.makeText(this, "Okay", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
        }
    }
}