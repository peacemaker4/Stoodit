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
import com.androidnetworking.widget.ANImageView
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.Note
import java.util.ArrayList

class CoursesAdapter
    (private val mArrayList: ArrayList<Course>) :
    RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCourse = mArrayList[position]

        holder.subject.text = currentCourse.subject
        holder.credit.text = currentCourse.credit.toString()
        holder.total.text = currentCourse.total.toString()

    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subject: TextView
        val credit: TextView
        val total: TextView

        init {
            subject = itemView.findViewById(R.id.subject_id)
            credit = itemView.findViewById(R.id.credit_id)
            total = itemView.findViewById(R.id.total_id)
        }
    }

    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "CourseAdapter"
    }
}