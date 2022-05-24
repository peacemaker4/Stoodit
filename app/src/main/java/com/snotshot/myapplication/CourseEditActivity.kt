package com.snotshot.myapplication

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityCourseFormBinding
import com.snotshot.myapplication.models.Course


class CourseEditActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCourseFormBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "courses"

    private var subject = ""
    private var credit = ""
    private var total = ""
    private var uid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "New Course"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val extras = intent.extras
        if (extras != null) {
            subject = extras.getString("subject").toString()
            credit = extras.getString("credit").toString()
            total = extras.getString("total").toString()
            uid = extras.getString("uid").toString()

            binding.subjectInput.setText(subject)
            binding.creditInput.setText(credit)
            binding.totalInput.setText(total)
        }

        binding.addCourseBtn.setText("Save")
        binding.addCourseBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_icon_done, 0)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        database = Firebase.database(url).reference

        binding.addCourseBtn.setOnClickListener{
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
        val course = Course(subject, credit.toInt(), total.toInt())

        database.child(path).child(firebaseUser!!.uid).child(uid).setValue(course).addOnSuccessListener { e->
//            Toast.makeText(this, "Course been added", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener{ e->
            Toast.makeText(this, "Error while adding the note: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.snotshot.myapplication.R.menu.item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == com.snotshot.myapplication.R.id.action_delete){
            database.child(path).child(firebaseUser!!.uid).child(uid).removeValue().addOnSuccessListener { e->
                onBackPressed()
            }.addOnFailureListener{ e->
                Toast.makeText(this, "Unable to delete course: $e", Toast.LENGTH_SHORT).show()
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}