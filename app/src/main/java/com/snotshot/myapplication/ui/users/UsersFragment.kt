package com.snotshot.myapplication.ui.users

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
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
import android.widget.SearchView
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList


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
    private var searchList: ArrayList<User>? = null

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

        setHasOptionsMenu(true)

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path)
        }

        recyclerView = binding.usersList
        recyclerView!!.setLayoutManager(LinearLayoutManager(binding.root.context))

        usersList = ArrayList()
        searchList = ArrayList()

        val usersListener = object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList = ArrayList()
                for (noteSnapshot in dataSnapshot.children) {
                    val user = noteSnapshot.getValue<User>()!! as User
                    usersList!!.add(user)
                }
                searchList!!.addAll(usersList!!)
                if(_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    usersAdapter = UsersAdapter(binding.root.context, searchList!!)
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

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search, menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                searchList!!.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    usersList!!.forEach{
                        if(it.username!!.toLowerCase(Locale.getDefault()).contains(searchText) || it.email!!.toLowerCase(
                                Locale.getDefault()).contains(searchText)){
                            searchList!!.add(it)
                        }
                    }
                    recyclerView!!.adapter!!.notifyDataSetChanged()
                }
                else{
                    searchList!!.clear()
                    searchList!!.addAll(usersList!!)
                    recyclerView!!.adapter!!.notifyDataSetChanged()
                }

                return false
            }

        })

        super.onCreateOptionsMenu(menu, menuInflater)
    }

    private fun checkUser() {
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