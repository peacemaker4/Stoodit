package com.snotshot.myapplication.ui.notes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.snotshot.myapplication.LoginActivity
import com.snotshot.myapplication.R
import android.app.Activity
import android.content.ContentValues
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.NoteFormActivity
import com.snotshot.myapplication.databinding.FragmentNotesBinding
import com.snotshot.myapplication.models.Note
import com.snotshot.myapplication.ui.profile.NotesFragmentModel
import android.annotation.SuppressLint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.snotshot.myapplication.adapters.NoteAdapter
import com.snotshot.myapplication.extensions.SpacesItemDecoration


class NotesFragment : Fragment() {

    private lateinit var notesViewModel: NotesFragmentModel
    private var _binding: FragmentNotesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "notes"

    private var email = ""

    private var noteAdapter: NoteAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var notesList: ArrayList<Note>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notesViewModel =
            ViewModelProvider(this).get(NotesFragmentModel::class.java)

        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val textView: TextView = binding.textProfile
        notesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path).child(firebaseUser.uid)
        }

        recyclerView = binding.notesList
        recyclerView!!.setLayoutManager(LinearLayoutManager(binding.root.context))

        notesList = ArrayList()

        val decoration = SpacesItemDecoration(16)
        recyclerView!!.addItemDecoration(decoration)

        val notesListener = object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notesList = ArrayList()
                for (noteSnapshot in dataSnapshot.getChildren()) {
                    var note = noteSnapshot.getValue<Note>()!!
                    notesList!!.add(note)
                }
                notesList!!.reverse()
                if(_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    noteAdapter = NoteAdapter(notesList!!)
                    recyclerView!!.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    recyclerView!!.adapter = noteAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
            }
        }
        database.addValueEventListener(notesListener)


        val addBtn = root.findViewById(R.id.addNotesBtn) as FloatingActionButton?
        if (addBtn != null) {
            addBtn.setOnClickListener(View.OnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, NoteFormActivity::class.java))
            })

        }

        return root
    }

    private fun checkUser() {
        //check if user logged in
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            email = firebaseUser.email.toString();
        }
        else{
            binding.root.context.startActivity(Intent(binding.root.context, LoginActivity::class.java))
            val activity = context as Activity?
            activity!!.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}