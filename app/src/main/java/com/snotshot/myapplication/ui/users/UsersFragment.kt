package com.snotshot.myapplication.ui.users

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
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
import com.snotshot.myapplication.LoginActivity
import com.snotshot.myapplication.R
import com.snotshot.myapplication.adapters.UsersAdapter
import com.snotshot.myapplication.databinding.FragmentUsersBinding
import com.snotshot.myapplication.models.User

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast





class UsersFragment : Fragment() {

    private lateinit var usersFragmentModel: UsersFragmentModel
    private var _binding: FragmentUsersBinding? = null

    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    private var email = ""

    private var usersAdapter: UsersAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var usersList: ArrayList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        usersFragmentModel =
            ViewModelProvider(this).get(UsersFragmentModel::class.java)

        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val textView: TextView = binding.textUsers
        usersFragmentModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path)
        }

        recyclerView = binding.usersList
        recyclerView!!.setLayoutManager(LinearLayoutManager(binding.root.context))

        usersList = ArrayList()

        val usersListener = object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList = ArrayList()
                for (noteSnapshot in dataSnapshot.children) {
                    val user = noteSnapshot.getValue<User>()!!
                    usersList!!.add(user)
                }
                if(_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    usersAdapter = UsersAdapter(usersList!!)
                    recyclerView!!.adapter = usersAdapter

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
            }
        }
        database.addValueEventListener(usersListener)



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