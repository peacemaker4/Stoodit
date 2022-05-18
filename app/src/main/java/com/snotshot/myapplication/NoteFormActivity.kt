package com.snotshot.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityNoteFormBinding
import android.R
import android.app.ProgressDialog
import android.opengl.Visibility
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.internal.ContextUtils.getActivity
import com.snotshot.myapplication.models.Note
import com.snotshot.myapplication.models.User
import com.snotshot.myapplication.ui.notes.NotesFragment


class NoteFormActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNoteFormBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "notes"

    private var subject = ""
    private var note = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "New Note"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)


        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database(url).reference

        binding.addNotesBtn.setOnClickListener{
            validateData()
        }

    }

    private fun validateData(){
        subject = binding.subjectInput.text.toString().trim()
        note = binding.noteInput.text.toString().trim()

        if(TextUtils.isEmpty(note)){
        }
        else{
            addNote()
        }
    }

    private fun addNote(){
        val firebaseUser = firebaseAuth.currentUser

        val note = Note(subject, note)

        database.child(path).child(firebaseUser!!.uid).push().setValue(note).addOnSuccessListener { e->
//            Toast.makeText(this, "Note been added", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener{ e->
            Toast.makeText(this, "Error while adding the note: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}