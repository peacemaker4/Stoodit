package com.snotshot.myapplication.ui.courses

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.CourseFormActivity
import com.snotshot.myapplication.LoginActivity
import com.snotshot.myapplication.NoteFormActivity
import com.snotshot.myapplication.R
import com.snotshot.myapplication.adapters.CoursesAdapter
import com.snotshot.myapplication.adapters.NoteAdapter
import com.snotshot.myapplication.databinding.FragmentCoursesBinding
import com.snotshot.myapplication.databinding.FragmentHomeBinding
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.Note

class CoursesFragment : Fragment() {

    private lateinit var coursesFragmentModel: CoursesFragmentModel
    private var _binding: FragmentCoursesBinding? = null

    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "courses"

    private var email = ""

    private var coursesAdapter: CoursesAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var coursesList: ArrayList<Course>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        coursesFragmentModel =
            ViewModelProvider(this).get(CoursesFragmentModel::class.java)

        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val textView: TextView = binding.textCourses
        coursesFragmentModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path).child(firebaseUser.uid)
        }

        recyclerView = binding.coursesList
        recyclerView!!.setLayoutManager(LinearLayoutManager(binding.root.context))

        coursesList = ArrayList()

        val notesListener = object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coursesList = ArrayList()
                for (noteSnapshot in dataSnapshot.children) {
                    val course = noteSnapshot.getValue<Course>()!!
                    coursesList!!.add(course)
                }
                if(_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    coursesAdapter = CoursesAdapter(coursesList!!)
                    recyclerView!!.adapter = coursesAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
            }
        }
        database.addValueEventListener(notesListener)

        val addBtn = root.findViewById(R.id.addCourseBtn) as FloatingActionButton?
        if (addBtn != null) {
            addBtn.setOnClickListener(View.OnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, CourseFormActivity::class.java))
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