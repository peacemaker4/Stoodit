package com.snotshot.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.snotshot.myapplication.adapters.NewUsersChatAdapter
import com.snotshot.myapplication.databinding.ActivityChatFormBinding
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.models.UserChat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class ChatFormActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatFormBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val usersPath = "users"
    private val usersChatsPath = "users_chats"
    private val chatsPath = "chats"

    private var email = ""

    private var usersAdapter: NewUsersChatAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var usersList: ArrayList<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Start chating"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference
        }

        recyclerView = binding.usersList
        recyclerView!!.layoutManager = LinearLayoutManager(binding.root.context)

        usersList = ArrayList()

        database.child(usersPath).addValueEventListener(object : ValueEventListener {

            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList = ArrayList()
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue<User>()!!
                    if(user.uid != firebaseUser!!.uid)
                        usersList!!.add(user)
                }

                if (binding != null) {
                    binding.progressBar.visibility = View.GONE
                    usersAdapter = NewUsersChatAdapter(binding.root.context, usersList!!)
                    recyclerView!!.adapter = usersAdapter

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
            }
        })


    }

    private fun checkUser() {
        //check if user logged in
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            email = firebaseUser.email.toString();
        }
        else{
            binding.root.context.startActivity(Intent(binding.root.context, LoginActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}