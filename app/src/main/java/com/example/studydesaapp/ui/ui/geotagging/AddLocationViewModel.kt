package com.example.studydesaapp.ui.ui.geotagging

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class AddLocationViewModel : ViewModel(){
    private val scoreFaculty = MutableLiveData<String>()
    private val nameFaculty = MutableLiveData<String>()
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    var faculty = ""
    var score = ""

    fun loadScoreFacultyFromFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        val currentUser = firebaseAuth.currentUser
        reference.child("Mahasiswa").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(currentUser?.phoneNumber!!.toString()).exists()){
                    faculty = snapshot.child(currentUser?.phoneNumber!!.toString()).child("faculty").value.toString()
                                Log.d("Cari mahasiswa", faculty)
                    nameFaculty.postValue(faculty)
                }else{
                    Log.d("Cari mahasiswa", "Gagal")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })

        reference.child("Faculty").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(faculty).exists()){
                    score = snapshot.child(faculty).child("score").value.toString()
                    Log.d("Cari fakultas", score)
                    scoreFaculty.postValue(score)
                }else{
                    Log.d("Cari fakultas", "Gagal")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })
    }

    fun getScoreFaculty(): LiveData<String> {
        return scoreFaculty
    }

    fun getNameFaculty(): LiveData<String> {
        return nameFaculty
    }
}