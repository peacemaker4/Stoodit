package com.snotshot.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.snotshot.myapplication.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityRegisterBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //Firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private var username = ""
    private var email = ""
    private var password = ""
    private var passwordConfirm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //register button click
        binding.registerBtn.setOnClickListener{
            validateData();
        }
    }
    private fun validateData(){
        //getting inputs
        username = binding.usernameInput.text.toString().trim()
        email = binding.emailInput.text.toString().trim()
        password = binding.passwordInput.text.toString().trim()
        passwordConfirm = binding.passwordConfirmInput.text.toString().trim()

        //validation
        if(TextUtils.isEmpty(username)){
            binding.usernameInput.error = "Enter username"
        }
        else if(username.length < 3){
            binding.usernameInput.error = "Username should be at least 3 characters long"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailInput.error = "Invalid email"
        }
        else if(TextUtils.isEmpty(password)){
            binding.passwordInput.error = "Enter password"
        }
        else if(password.length < 8){
            binding.passwordInput.error = "Password length should be at least 8 symbols"
        }
        else if(password != passwordConfirm){
            binding.passwordInput.error = "Passwords do not match"
            binding.passwordConfirmInput.error = "Passwords do not match"
        }
        else{
            firebaseSignUp()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun firebaseSignUp() {
        //progress bar show

        //account register
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //register success
                progressDialog.dismiss()

                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Account created with an email $email", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener{
                //register failed
                e ->
                progressDialog.dismiss()

                Toast.makeText(this, "Register failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}