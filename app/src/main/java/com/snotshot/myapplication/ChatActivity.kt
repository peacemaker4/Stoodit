package com.snotshot.myapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.adapters.MessagesAdapter
import com.snotshot.myapplication.databinding.ActivityChatBinding
import com.snotshot.myapplication.extensions.SpacesItemDecoration
import com.snotshot.myapplication.models.Message
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.models.UserChat
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity: AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    //Firebase db
    lateinit var database: DatabaseReference
    lateinit var usersDatabase: DatabaseReference
    lateinit var chatsDatabase: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val usersPath = "users"
    private val chatsPath = "chats"
    private val usersChatsPath = "users_chats"

    private var messagesAdapter: MessagesAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var messagesList: ArrayList<Message>? = null

    var contactUid = ""
    var chatUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        val firebaseUser = firebaseAuth.currentUser

        lateinit var storageRef: StorageReference

        database = Firebase.database(url).reference



        val extras = intent.extras
        if (extras != null) {
            contactUid = extras.getString("uid").toString()
            chatUid = extras.getString("chat_uid").toString().trim()
        }

        if (contactUid.isNotEmpty()) {
            usersDatabase = database.child(usersPath).child(contactUid)

            usersDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue<User>()
                    binding.contactNameId.text = user!!.username

                    if(!user.picture.isNullOrBlank()){
                        storageRef = FirebaseStorage.getInstance().reference.child("users").child(user.picture.toString())
                        var localFile: File = File.createTempFile(user.uid, user.picture!!.replace(firebaseUser!!.uid + ".", ""))
                        storageRef.getFile(localFile).addOnSuccessListener {
                            val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            binding.profileImage.setImageBitmap(bitmap)
                        }.addOnFailureListener{
                            Toast.makeText(binding.root.context, "Error retrieving image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
                }
            })
        }

        recyclerView = binding.messagesRecycler
        recyclerView!!.layoutManager = LinearLayoutManager(binding.root.context)
        val decoration = SpacesItemDecoration(16)
        recyclerView!!.addItemDecoration(decoration)

        chatsDatabase = database.child(chatsPath)


        chatsDatabase.child(chatUid).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList = ArrayList()
                for(messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue<Message>()!!
                    messagesList!!.add(message)
                }
                messagesAdapter = MessagesAdapter(binding.root.context, messagesList!!)
                recyclerView!!.layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                recyclerView!!.adapter = messagesAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", error.toException())
            }
        })

        binding.backBtn.setOnClickListener{
            this.finish()
//            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.contactNameId.setOnClickListener{
            val intent = Intent(this, ProfilePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("uid", contactUid)
            this.startActivity(intent)
        }

        binding.profileImage.setOnClickListener{
            val intent = Intent(this, ProfilePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("uid", contactUid)
            this.startActivity(intent)
        }

        binding.sendMessageBtn.setOnClickListener{
            val currentDate = Date()
            val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val dateText: String = dateFormat.format(currentDate)
            val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeText: String = timeFormat.format(currentDate)
            val time = "$dateText $timeText"

            val message: String = binding.userMessage.text.toString()
            if (message != ""){
                sendMessage(firebaseUser!!.uid, contactUid, message.trim(), timeText)
            } else {
                Toast.makeText(this, "You can't send empty message", Toast.LENGTH_SHORT).show()
            }
            binding.userMessage.setText("");
        }


    }


    private fun sendMessage(sender: String, receiver: String, text: String, time: String) {
        if(chatUid == "") {
            chatUid = chatsDatabase.push().key.toString()
            chatsDatabase.child(chatUid).child("correspondent1").setValue(sender)
            chatsDatabase.child(chatUid).child("correspondent2").setValue(receiver)
        }

        val message = Message(text,time,sender)

        chatsDatabase.child(chatUid).child("messages").push().setValue(message);

        val userChat1 = UserChat(message, chatUid, receiver)
        database.child(usersChatsPath).child(sender).child(chatUid).setValue(userChat1)

        val userChat2 = UserChat(message, chatUid, sender)
        database.child(usersChatsPath).child(receiver).child(chatUid).setValue(userChat2)

        chatsDatabase.child(chatUid).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList = ArrayList()
                for(messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue<Message>()!!
                    messagesList!!.add(message)
                }
                messagesAdapter = MessagesAdapter(binding.root.context, messagesList!!)
                recyclerView!!.layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                recyclerView!!.adapter = messagesAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", error.toException())
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}