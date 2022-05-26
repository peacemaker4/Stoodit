package com.snotshot.myapplication.ui.chats

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.ChatFormActivity
import com.snotshot.myapplication.LoginActivity
import com.snotshot.myapplication.R
import com.snotshot.myapplication.adapters.ChatsAdapter
import com.snotshot.myapplication.databinding.FragmentChatsBinding
import com.snotshot.myapplication.extensions.SpacesItemDecoration
import com.snotshot.myapplication.models.UserChat

class ChatsFragment : Fragment() {

    private lateinit var chatsFragmentModel: ChatsFragmentModel
    private var _binding: FragmentChatsBinding? = null

    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users_chats"

    private var email = ""

    private var chatsAdapter: ChatsAdapter? = null
    private var recyclerView: RecyclerView? = null

    private var chatsList: ArrayList<UserChat>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatsFragmentModel =
            ViewModelProvider(this).get(ChatsFragmentModel::class.java)

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val textView: TextView = binding.textChats
        chatsFragmentModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        recyclerView = binding.chatsList
        recyclerView!!.layoutManager = LinearLayoutManager(binding.root.context)
        val decoration = SpacesItemDecoration(16)
        recyclerView!!.addItemDecoration(decoration)
        chatsList = ArrayList()


        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path).child(firebaseUser.uid)
        }


        val chatsListener = object : ValueEventListener {
            @SuppressLint("ResourceType")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatsList = ArrayList()
                for (chatSnapshot in dataSnapshot.children) {
                    val userChat = chatSnapshot.getValue<UserChat>()!!
                    chatsList!!.add(userChat)
                }
                chatsList!!.reverse()
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    chatsAdapter = ChatsAdapter(binding.root.context, chatsList!!)
                    recyclerView!!.layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    recyclerView!!.adapter = chatsAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "Read failed", databaseError.toException())
            }
        }
        database.addValueEventListener(chatsListener)

        val addBtn = root.findViewById(R.id.addChatBtn) as FloatingActionButton?
        if (addBtn != null) {
            addBtn.setOnClickListener(View.OnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, ChatFormActivity::class.java))
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