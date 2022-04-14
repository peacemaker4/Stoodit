package com.snotshot.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.snotshot.myapplication.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityWelcomeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //check for user authorization
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Register
        binding.signUpBtn.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Login
        binding.signInBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun checkUser() {
        //if user is already logged in go to main activity
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}