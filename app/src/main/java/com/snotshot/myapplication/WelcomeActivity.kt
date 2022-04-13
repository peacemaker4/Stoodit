package com.snotshot.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.snotshot.myapplication.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Register
        binding.signUpBtn.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Login
        binding.signInBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}