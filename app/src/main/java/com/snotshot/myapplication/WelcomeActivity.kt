package com.snotshot.myapplication

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityWelcomeBinding
import com.snotshot.myapplication.models.User
import org.jetbrains.anko.startActivityForResult

class WelcomeActivity : AppCompatActivity() {

    private lateinit var account: GoogleSignInAccount
    private lateinit var googleSignInClient: GoogleSignInClient

    //View binding
    private lateinit var binding: ActivityWelcomeBinding

    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    private lateinit var progressDialog: ProgressDialog

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

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging in account...")
        progressDialog.setCanceledOnTouchOutside(false)

        createGoogleRequest()
        database = Firebase.database(url).reference

        // Login with Google
        binding.signInGoogle.setOnClickListener{
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Companion.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        progressDialog.show()

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException){
                Log.d(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task->
                if(task.isSuccessful){
                    val user = firebaseAuth.currentUser
                    Log.d(ContentValues.TAG, "singInWithCredentian:success")
                    updateUI(user)
                }
                else{
                    Log.d(ContentValues.TAG, "singInWithCredentian:fail", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            val dbuser = User(user!!.uid, account.displayName, user.email, "None", "1", "None", "")

            database.child(path).child(dbuser.uid.toString()).setValue(dbuser).addOnSuccessListener { e->
                progressDialog.dismiss()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener{ e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error while creating the user: $e", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun createGoogleRequest(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("784694790345-ai5vaq01nnur87889h4q5duasnric75l.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }


    private fun checkUser() {
        //if user is already logged in go to main activity
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){


            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}