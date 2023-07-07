package com.bbt2000.boilerplate.demos.viewmodel

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class UsersActivity : AppCompatActivity() {
    // ?
    val model: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val usersObserver = Observer<List<User>> { users ->
            println(users)
        }
        model.getUsers().observe(this, usersObserver)
    }
}
