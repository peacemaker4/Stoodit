package com.snotshot.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.snotshot.myapplication.databinding.ActivityRegisterBinding

class WelcomeActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        startActivity(Intent(this, RegisterActivity::class.java))
    }
}