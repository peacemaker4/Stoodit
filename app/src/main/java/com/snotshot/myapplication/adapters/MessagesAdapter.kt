package com.snotshot.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.R
import com.snotshot.myapplication.models.Message


class MessagesAdapter
    (private val mContext: Context, private val mArrayList: ArrayList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    private val leftMessage = 0
    private val rightMessage = 1

    var user: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesAdapter.ViewHolder {
        val view: View

        return if (viewType == rightMessage) {
            view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessagesAdapter.ViewHolder, position: Int) {
        val currentMessage = mArrayList[position]
        holder.message.text = currentMessage.text
        holder.time.text = currentMessage.time
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        user = FirebaseAuth.getInstance().currentUser
        return if (mArrayList[position].sender.equals(user?.uid)) 1
        else 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.message_id)
        var time: TextView = itemView.findViewById(R.id.time_id)
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "UsersAdapter"
    }
}
