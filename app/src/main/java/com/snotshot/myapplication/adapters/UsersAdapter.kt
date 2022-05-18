package com.snotshot.myapplication.adapters

import android.content.Context
import com.snotshot.myapplication.models.Article
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.snotshot.myapplication.R
import android.content.Intent
import android.view.View
import com.snotshot.myapplication.WebActivity
import android.widget.TextView
import android.widget.Toast
import com.snotshot.myapplication.models.User
import java.util.ArrayList

class UsersAdapter
    (private val mArrayList: ArrayList<User>) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = mArrayList[position]

        holder.username.text = currentUser.username
        holder.university.text = currentUser.university

    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView
        val university: TextView

        init {
            username = itemView.findViewById(R.id.username_id)
            university = itemView.findViewById(R.id.university_id)
        }
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "UsersAdapter"
    }

}