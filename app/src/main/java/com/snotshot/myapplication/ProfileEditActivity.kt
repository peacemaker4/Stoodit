package com.snotshot.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityNoteFormBinding
import android.R
import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.Color
import android.opengl.Visibility
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.snotshot.myapplication.adapters.CustomSpinnerAdapter
import com.snotshot.myapplication.databinding.ActivityProfileEditBinding
import com.snotshot.myapplication.models.Note
import com.snotshot.myapplication.models.University
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.ui.notes.NotesFragment


class ProfileEditActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    private var username = ""
    private var university = ""
    private var year = ""
    private var group = ""
    private var description = ""
    private var user = User()

    private lateinit var spinnerAdapter: CustomSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Profile Edit"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path).child(firebaseUser.uid)
        }
        user = User()

        var universitiesList = listOf(
            University("None", "Not selected", com.snotshot.myapplication.R.drawable.ic_launcher_background),
            University("AITU", "Astana IT University", com.snotshot.myapplication.R.drawable.aitu),
        )

        spinnerAdapter = CustomSpinnerAdapter(this, universitiesList)
        binding.spinner.adapter = spinnerAdapter

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>()!!
                binding.loadingBar.visibility = View.GONE
                binding.textProfile.setText(user.username)
                binding.descriptionText.setText(user.description)
                binding.saveBtn.isEnabled = true
                binding.textProfile.isEnabled = true
                binding.descriptionText.isEnabled = true
                binding.spinner.isEnabled = true
                binding.groupText.isEnabled = true
                binding.yearText.isEnabled = true
                binding.groupText.setText(user.group)
                binding.yearText.setText(user.year)


                var id = 0
                for (u in universitiesList){
                    if(u.name.equals(user.university)){
                        break
                    }
                    id++
                }
                binding.spinner.setSelection(id)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }



        database.addValueEventListener(userListener)

        binding.saveBtn.setOnClickListener{
            validateData()
        }
    }

    private fun validateData(){
        username = binding.textProfile.text.toString().trim()
        university = (binding.spinner.selectedItem as University).name
        group = binding.groupText.text.toString().trim()
        year = binding.yearText.text.toString().trim()
        description = binding.descriptionText.text.toString().trim()
        if(TextUtils.isEmpty(username)){
            binding.textProfile.error = "Enter username"
        }
        else if(username.equals(user.username) && description.equals(user.description) && group.equals(user.group) && year.equals(user.year) && university.equals(user.university)){
            Toast.makeText(this, "Nothing changed dude", Toast.LENGTH_SHORT).show()
        }
        else{
            saveChanges()
        }
    }

    private fun saveChanges(){

        val updated_user = User(firebaseUser.uid, username, firebaseUser.email, university, year, group, description)

        database.setValue(updated_user).addOnSuccessListener { e->
            var toast = Toast.makeText(this, "User info updated!", Toast.LENGTH_SHORT)
            var view = toast.view
            view?.setBackgroundColor(Color.parseColor("#009afe"))
            toast.show()
        }.addOnFailureListener{ e->
            Toast.makeText(this, "Error while creating the user: $e", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}