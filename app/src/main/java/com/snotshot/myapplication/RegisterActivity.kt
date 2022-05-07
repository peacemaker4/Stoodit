package com.snotshot.myapplication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityRegisterBinding
import com.snotshot.myapplication.models.User

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

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

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
        checkUser()

        database = Firebase.database(url).reference

        //register button click
        binding.registerBtn.setOnClickListener{
            validateData();
        }
        //sign in link click
        binding.signInLink.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
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

                val user = User(firebaseUser!!.uid, username, email, "None", "", "", "")

                database.child(path).child(user.uid.toString()).setValue(user).addOnSuccessListener { e->
                    Toast.makeText(this, "Account created with an email $email", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{ e->
                    Toast.makeText(this, "Error while creating the user: $e", Toast.LENGTH_SHORT).show()
                }

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

    private fun checkUser() {
        //if user is already logged in go to main activity
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}