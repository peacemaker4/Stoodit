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
import android.widget.Toast
import com.snotshot.myapplication.NoteEditActivity
import com.snotshot.myapplication.models.Note
import java.util.ArrayList

class NoteAdapter
    (private val mContext: Context, private val mArrayList: ArrayList<Note>) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNote = mArrayList[position]

        holder.subject.text = currentNote.subject
        holder.note.text = currentNote.note
        if(currentNote.subject.isNullOrEmpty()){
            holder.subject.visibility = View.GONE
        }
        holder.itemView.setOnClickListener{val intent = Intent(mContext, NoteEditActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("subject", currentNote.subject)
            intent.putExtra("note", currentNote.note)
            intent.putExtra("uid", currentNote.uid)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subject: TextView
        val note: TextView

        init {
            subject = itemView.findViewById(R.id.subject_id)
            note = itemView.findViewById(R.id.note_id)
        }

    }


    companion object {
        // setting the TAG for debugging purposes
        private const val TAG = "NoteAdapter"
    }
}