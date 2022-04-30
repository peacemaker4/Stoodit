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
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.snotshot.myapplication.NoteFormActivity
import com.snotshot.myapplication.databinding.FragmentNotesBinding
import com.snotshot.myapplication.ui.profile.NotesFragmentModel


class NotesFragment : Fragment() {

    private lateinit var notesViewModel: NotesFragmentModel
    private var _binding: FragmentNotesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    private var email = ""

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