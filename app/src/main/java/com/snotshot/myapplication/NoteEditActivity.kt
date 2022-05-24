package com.snotshot.myapplication

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.databinding.ActivityNoteFormBinding
import com.snotshot.myapplication.models.Note


class NoteEditActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNoteFormBinding

    //ActionBar
    private lateinit var actionBar: ActionBar


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "notes"

    private var subject = ""
    private var note = ""
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Edit Note"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val extras = intent.extras
        if (extras != null) {
            subject = extras.getString("subject").toString()
            note = extras.getString("note").toString()
            uid = extras.getString("uid").toString()

            binding.subjectInput.setText(subject)
            binding.noteInput.setText(note)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        database = Firebase.database(url).reference
        firebaseUser = firebaseAuth.currentUser!!

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

        val note = Note(subject, note)

        database.child(path).child(firebaseUser!!.uid).child(uid).setValue(note).addOnSuccessListener { e->
            onBackPressed()
        }.addOnFailureListener{ e->
            Toast.makeText(this, "Error while adding the note: $e", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete){
            database.child(path).child(firebaseUser!!.uid).child(uid).removeValue().addOnSuccessListener { e->
                onBackPressed()
            }.addOnFailureListener{ e->
                Toast.makeText(this, "Unable to delete note: $e", Toast.LENGTH_SHORT).show()
            }

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}