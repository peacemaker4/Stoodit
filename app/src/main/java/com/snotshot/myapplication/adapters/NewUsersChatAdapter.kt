package com.snotshot.myapplication.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.snotshot.myapplication.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.ChatActivity
import com.snotshot.myapplication.ProfilePageActivity
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.models.UserChat
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.ArrayList

class NewUsersChatAdapter
    (private val mContext: Context, private val mArrayList: ArrayList<User>) :
    RecyclerView.Adapter<NewUsersChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_chat_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = mArrayList[position]

        holder.username.text = currentUser.username

        lateinit var storageRef: StorageReference

        if(!currentUser.picture.isNullOrBlank()){
            storageRef = FirebaseStorage.getInstance().reference.child("users").child(currentUser.picture.toString())
            var localFile: File = File.createTempFile(currentUser.uid, currentUser.picture!!.replace(currentUser.uid + ".", ""))
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.profileImage.setImageBitmap(bitmap)
            }
        }


        var chatUid = ""


        val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
        val path = "users_chats"
        val database: DatabaseReference = Firebase.database(url).reference.child(path).child(currentUser.uid.toString())

        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue<UserChat>()
                    if(chat!!.contact_uid == FirebaseAuth.getInstance().currentUser!!.uid) {
                        chatUid = chat.chat_uid.toString()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })


        holder.itemView.setOnClickListener{val intent = Intent(mContext, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("chat_uid", chatUid)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username_id)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "UsersAdapter"
    }

}