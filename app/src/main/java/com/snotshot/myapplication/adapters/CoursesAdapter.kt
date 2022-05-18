package com.snotshot.myapplication.adapters

import android.content.Context
import com.snotshot.myapplication.models.Article
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import com.snotshot.myapplication.R
import android.content.Intent
import android.graphics.Color
import android.view.View
import com.snotshot.myapplication.WebActivity
import android.widget.TextView
import com.androidnetworking.widget.ANImageView
import com.snotshot.myapplication.CourseEditActivity
import com.snotshot.myapplication.NoteEditActivity
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.Note
import java.util.ArrayList

class CoursesAdapter
    (private val mContext: Context, private val mArrayList: ArrayList<Course>) :
    RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCourse = mArrayList[position]

        holder.subject.text = currentCourse.subject
        holder.credit.text = currentCourse.credit.toString()

        holder.total.text = currentCourse.total.toString() + "%"
        if(currentCourse.total!! >= 90){
            holder.total.setTextColor(Color.parseColor("#00A590"))
        }
        else if(currentCourse.total!! < 90 && currentCourse.total!! >= 70){
            holder.total.setTextColor(Color.parseColor("#009afe"))
        }
        else if(currentCourse.total!! < 70 && currentCourse.total!! >= 50){
            holder.total.setTextColor(Color.parseColor("#8A8A8A"))
        }
        else{
            holder.total.setTextColor(Color.parseColor("#CA0000"))
        }
        holder.itemView.setOnClickListener{val intent = Intent(mContext, CourseEditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("subject", currentCourse.subject)
            intent.putExtra("credit", currentCourse.credit.toString())
            intent.putExtra("total", currentCourse.total.toString())
            intent.putExtra("uid", currentCourse.uid)
            mContext.startActivity(intent)
        }
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