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
import android.opengl.Visibility
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.internal.ContextUtils.getActivity
import com.snotshot.myapplication.databinding.ActivityCourseFormBinding
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.Note
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.ui.notes.NotesFragment


class CourseFormActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCourseFormBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "courses"

    private var subject = ""
    private var credit = ""
    private var total = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "New Course"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database(url).reference

        binding.addNotesBtn.setOnClickListener{
            validateData()
        }

    }

    private fun validateData(){
        subject = binding.subjectInput.text.toString().trim()
        credit = binding.creditInput.text.toString().trim()
        total = binding.totalInput.text.toString().trim()

        if(TextUtils.isEmpty(subject)){
            binding.subjectInput.error = "Enter subject"
        }
        else if(TextUtils.isEmpty(credit)){
            binding.creditInput.error = "Enter credits amount"
        }
        else if(TextUtils.isEmpty(total)){
            binding.totalInput.error = "Enter total"
        }
        else{
            addCourse()
        }
    }

    private fun addCourse(){
        val firebaseUser = firebaseAuth.currentUser

        val course = Course(subject, credit, total)

        database.child(path).child(firebaseUser!!.uid).push().setValue(course).addOnSuccessListener { e->
            Toast.makeText(this, "Course been added", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener{ e->
            Toast.makeText(this, "Error while adding the note: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}