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
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.adapters.CustomSpinnerAdapter
import com.snotshot.myapplication.databinding.ActivityProfileEditBinding
import com.snotshot.myapplication.models.Note
import com.snotshot.myapplication.models.University
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.ui.notes.NotesFragment
import android.webkit.MimeTypeMap

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.UploadTask

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import com.snotshot.myapplication.databinding.ActivityProfilePageBinding
import com.snotshot.myapplication.databinding.FragmentProfileBinding
import com.snotshot.myapplication.models.Course
import java.io.File


class ProfilePageActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    private var uid = ""
    private var user = User()

    // for count gpa
    private val gpaPath = "courses"
    lateinit var coursesDatabase: DatabaseReference

    lateinit var storageRef: StorageReference

    private lateinit var spinnerAdapter: CustomSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "User"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        val extras = intent.extras
        if (extras != null) {
            uid = extras.getString("uid").toString()
        }

//        val firebaseUser = firebaseAuth.currentUser
        if (!uid.isNullOrEmpty()) {
            database = Firebase.database(url).reference.child(path).child(uid)
            coursesDatabase = Firebase.database(url).reference.child(gpaPath).child(uid)
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>()!!
                if(binding != null){
                    binding.textProfile.setBackgroundColor(Color.WHITE)
                    binding.loadingBar.visibility = View.GONE

                    binding.textProfile.text = user.username
//                    binding.textEmail.text = user.email

                    actionBar.title = user.username

                    if(user.university!!.length > 0) {
                        binding.universityText.visibility = View.VISIBLE
                        binding.universityText.text = user.university
                    }
                    else{
                        binding.universityText.visibility = View.GONE
                    }
                    if(user.group!!.length > 0) {
                        binding.groupText.visibility = View.VISIBLE
                        binding.groupText.text = user.group
                    }
                    else{
                        binding.groupText.visibility = View.GONE
                    }
                    if(user.year!!.length > 0) {
                        binding.yearText.visibility = View.VISIBLE
                        binding.yearText.text = user.year + " year"
                    }
                    else{
                        binding.yearText.visibility = View.GONE
                    }
                    if(user.description!!.length > 0){
                        binding.descriptionText.visibility = View.VISIBLE
                        binding.descriptionText.text = user.description
                    }
                    else{
                        binding.descriptionText.visibility = View.GONE
                        binding.descriptionText.text = ""
                    }

                    if(!user.picture.isNullOrBlank()){
                        binding.pictureProgress.visibility = View.VISIBLE
                        binding.pictureProgressHorizontal.visibility = View.VISIBLE
                        storageRef = FirebaseStorage.getInstance().getReference().child(path).child(user.picture.toString())
                        var localFile: File = File.createTempFile(uid, user.picture!!.replace(uid + ".", ""))
                        storageRef.getFile(localFile).addOnProgressListener { p ->
                            val progress = (100 * p.bytesTransferred)/p.totalByteCount
                            binding.pictureProgressHorizontal.progress = progress.toInt()
                        }.addOnSuccessListener {
                            val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            binding.profileImage.setImageBitmap(bitmap)
                            binding.pictureProgress.visibility = View.GONE
                            binding.pictureProgressHorizontal.visibility = View.GONE
                        }.addOnFailureListener{
                            Toast.makeText(binding.root.context, "Error retrieving image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        binding.profileImage.setImageDrawable(resources.getDrawable(com.snotshot.myapplication.R.drawable.profile))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(userListener)

        val notesListener = object : ValueEventListener {
            @SuppressLint("ResourceType", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var total: Double = 0.0
                var credits: Int = 0
                for (noteSnapshot in dataSnapshot.children) {
                    val course = noteSnapshot.getValue<Course>()!!
                    val score = course.total!!

                    credits += course.credit?.toInt()!!
                    when {
                        score>=95 -> total += 4 * course.credit
                        score>=90 -> total += 3.67 * course.credit
                        score>=85 -> total += 3.33 * course.credit
                        score>=80 -> total += 3 * course.credit
                        score>=75 -> total += 2.67 * course.credit
                        score>=70 -> total += 2.33 * course.credit
                        score>=65 -> total += 2 * course.credit
                        score>=60 -> total += 1.67 * course.credit
                        score>=55 -> total += 1.33 * course.credit
                        score>=50 -> total += 1 * course.credit
                    }
                }
                if(credits!=0) total /= credits
                else total = 0.0
                if(binding != null) {
                    binding.gpaText.text = "%.2f".format(total)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "gpa ", databaseError.toException())
            }
        }
        coursesDatabase.addValueEventListener(notesListener)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}