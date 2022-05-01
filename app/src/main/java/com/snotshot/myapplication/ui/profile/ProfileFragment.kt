package com.snotshot.myapplication.ui.profile

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
import com.snotshot.myapplication.databinding.FragmentProfileBinding
import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.snotshot.myapplication.models.User


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: NotesFragmentModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    private var email = ""
    private var name = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(NotesFragmentModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            database = Firebase.database(url).reference.child(path).child(firebaseUser.uid)
        }
        var user = User()

        val textView: TextView = binding.textProfile

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>()!!
                binding.textProfile.setBackgroundColor(Color.WHITE)
                binding.loadingBar.visibility = View.GONE
                profileViewModel.text.observe(viewLifecycleOwner, Observer {
                    textView.text = user.username
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(userListener)



        //handle logout click
        val logBtn = root.findViewById(R.id.logout_btn) as Button?
        if (logBtn != null) {
            logBtn.setOnClickListener(View.OnClickListener {
                firebaseAuth.signOut()
                checkUser()
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