package com.example.studydesaapp.ui.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.studydesaapp.databinding.FragmentProfileBinding
import com.example.studydesaapp.ui.MainActivity
import com.example.studydesaapp.ui.ui.profile.leaderboard.LeaderboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var profileBinding: FragmentProfileBinding
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        //instance reference
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        val currentUser = firebaseAuth.currentUser

        reference.child("Mahasiswa").child(currentUser?.phoneNumber!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value.toString()
                val faculty = snapshot.child("faculty").value.toString()
                profileBinding.txtNameProfile.text = name
                profileBinding.txtFacultyProfile.text = faculty
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })

        profileBinding.btnLeaderboard.setOnClickListener {
            val intent = Intent(context, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        profileBinding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}