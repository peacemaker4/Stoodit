package com.snotshot.myapplication.adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.*
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.models.UserChat
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.ArrayList



class ChatsAdapter
    (private val mContext: Context, private val mArrayList: ArrayList<UserChat>) :
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }
    private lateinit var storageRef: StorageReference

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentChat = mArrayList[position]

        lateinit var database: DatabaseReference
        val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
        val path = "users"
        database = Firebase.database(url).reference.child(path).child(currentChat.contact_uid.toString())
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>()
                holder.contact_name.text = user!!.username
                if(!user.picture.isNullOrBlank()){
                    storageRef = FirebaseStorage.getInstance().reference.child(path).child(user.picture.toString())
                    var localFile: File = File.createTempFile(currentChat.contact_uid.toString(), user.picture.replace(currentChat.contact_uid.toString() + ".", ""))
                    storageRef.getFile(localFile).addOnSuccessListener {
                        val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        holder.profileImage.setImageBitmap(bitmap)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

        holder.progressBar.visibility = View.GONE

        val message = currentChat.last_message
        if (message != null) {
            if(message.sender == currentChat.contact_uid)
                holder.last_message.text = message.text
            else
                holder.last_message.text = "You: " + message.text
            holder.lastMessageTime.text = message.time
        }

        holder.itemView.setOnClickListener{val intent = Intent(mContext, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("uid", currentChat.contact_uid.toString())
            intent.putExtra("chat_uid", currentChat.chat_uid.toString())
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contact_name: TextView = itemView.findViewById(R.id.contact_name_id)
        val last_message: TextView = itemView.findViewById(R.id.last_message_id)
        val progressBar: ProgressBar = itemView.findViewById(R.id.name_loading_bar)
        val lastMessageTime: TextView = itemView.findViewById(R.id.last_message_time_id)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)

    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "ChatAdapter"
    }
}