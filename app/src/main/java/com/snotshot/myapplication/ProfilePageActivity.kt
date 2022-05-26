package com.snotshot.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.adapters.CustomSpinnerAdapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import com.snotshot.myapplication.databinding.ActivityProfilePageBinding
import com.snotshot.myapplication.models.*
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

    private val usersChatsPath = "users_chats"
    lateinit var usersChatsDatabase: DatabaseReference

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
            usersChatsDatabase = Firebase.database(url).reference.child(usersChatsPath).child(uid)
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>()!!
                if(binding != null){
                    binding.textProfile.setBackgroundColor(Color.WHITE)
                    binding.loadingBar.visibility = View.GONE

                    binding.textProfile.text = user.username
                    binding.textEmail.text = user.email

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

        var chatUid = ""
        val fuserUid = FirebaseAuth.getInstance().currentUser!!.uid

        usersChatsDatabase.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(chatSnapshot in snapshot.children) {
                    val userChat = chatSnapshot.getValue<UserChat>()
                    if(userChat!!.contact_uid == fuserUid) {
                        chatUid = userChat.chat_uid.toString()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        binding.chatBtn.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("uid", uid)
            intent.putExtra("chat_uid", chatUid)
            this.startActivity(intent)

        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}