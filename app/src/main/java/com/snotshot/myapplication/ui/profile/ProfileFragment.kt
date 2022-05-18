package com.snotshot.myapplication.ui.profile

import android.annotation.SuppressLint
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
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.snotshot.myapplication.MainActivity
import com.snotshot.myapplication.ProfileEditActivity
import com.snotshot.myapplication.adapters.CoursesAdapter
import com.snotshot.myapplication.models.Course
import com.snotshot.myapplication.models.User
import java.io.File


class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: NotesFragmentModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    //Firebase db
    private lateinit var database: DatabaseReference
    private val url = "https://studit-b2d9b-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val path = "users"

    // for count gpa
    private val gpaPath = "courses"
    lateinit var coursesDatabase: DatabaseReference

    private var email = ""

    lateinit var storageRef: StorageReference

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
            coursesDatabase = Firebase.database(url).reference.child(gpaPath).child(firebaseUser.uid)

        }
        var user = User()

        val textView: TextView = binding.textProfile

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue<User>()!!
                if(_binding != null){
                    binding.textProfile.setBackgroundColor(Color.WHITE)
                    binding.loadingBar.visibility = View.GONE
                    profileViewModel.text.observe(viewLifecycleOwner, Observer {
                        textView.text = user.username
                    })
                    if(user.university!!.length > 0) {
                        binding.universityText.visibility = View.VISIBLE
                        binding.universityText.text = user.university
                    }
                    else{
                        binding.universityText.visibility = View.GONE
                    }
                    if(user.group!!.length > 0) {
                        binding.groupText.visibility = View.VISIBLE
                        binding.groupText.text = user.group
                    }
                    else{
                        binding.groupText.visibility = View.GONE
                    }
                    if(user.year!!.length > 0) {
                        binding.yearText.visibility = View.VISIBLE
                        binding.yearText.text = user.year + " year"
                    }
                    else{
                        binding.yearText.visibility = View.GONE
                    }
                    if(user.description!!.length > 0){
                        binding.descriptionText.visibility = View.VISIBLE
                        binding.descriptionText.text = user.description
                    }
                    else{
                        binding.descriptionText.visibility = View.GONE
                        binding.descriptionText.text = ""
                    }
                    binding.editBtn.isEnabled = true


                    if(!user.picture.isNullOrBlank()){
                        binding.pictureProgress.visibility = View.VISIBLE
                        binding.pictureProgressHorizontal.visibility = View.VISIBLE
                        storageRef = FirebaseStorage.getInstance().getReference().child(path).child(user.picture.toString())
                        var localFile: File = File.createTempFile(firebaseUser!!.uid, user.picture!!.replace(firebaseUser!!.uid + ".", ""))
                        storageRef.getFile(localFile).addOnProgressListener { p ->
                            val progress = (100 * p.bytesTransferred)/p.totalByteCount
                            binding.pictureProgressHorizontal.progress = progress.toInt()
                        }.addOnSuccessListener {
                            val bitmap : Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            binding.profileImage.setImageBitmap(bitmap)
                            binding.pictureProgress.visibility = View.GONE
                            binding.pictureProgressHorizontal.visibility = View.GONE
                        }.addOnFailureListener{
                            Toast.makeText(binding.root.context, "Error retrieving image", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        binding.profileImage.setImageDrawable(resources.getDrawable(R.drawable.profile))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(userListener)

        val notesListener = object : ValueEventListener {
            @SuppressLint("ResourceType", "SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var total: Double = 0.0
                var credits: Int = 0
                for (noteSnapshot in dataSnapshot.children) {
                    val course = noteSnapshot.getValue<Course>()!!
                    val score = course.total!!

                    credits += course.credit?.toInt()!!
                    when {
                        score>=95 -> total += 4 * course.credit
                        score>=90 -> total += 3.67 * course.credit
                        score>=85 -> total += 3.33 * course.credit
                        score>=80 -> total += 3 * course.credit
                        score>=75 -> total += 2.67 * course.credit
                        score>=70 -> total += 2.33 * course.credit
                        score>=65 -> total += 2 * course.credit
                        score>=60 -> total += 1.67 * course.credit
                        score>=55 -> total += 1.33 * course.credit
                        score>=50 -> total += 1 * course.credit
                    }
                }
                if(credits!=0) total /= credits
                else total = 0.0
                if(_binding != null) {
                    binding.gpaText.text = "GPA: " + "%.2f".format(total)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "gpa ", databaseError.toException())
            }
        }
        coursesDatabase.addValueEventListener(notesListener)


        //handle logout click
        val logBtn = root.findViewById(R.id.logout_btn) as Button?
        if (logBtn != null) {
            logBtn.setOnClickListener(View.OnClickListener {
                firebaseAuth.signOut()
                checkUser()
            })
        }
        val editBtn = root.findViewById(R.id.edit_btn) as MaterialButton?
        if (editBtn != null) {
            editBtn.setOnClickListener(View.OnClickListener {
                binding.root.context.startActivity(Intent(binding.root.context, ProfileEditActivity::class.java))
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