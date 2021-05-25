package com.example.studydesaapp.ui.ui.profile.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studydesaapp.ui.entity.Faculty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class  LeaderboardViewModel : ViewModel() {
    private val listLeaderboard = MutableLiveData<ArrayList<Faculty>>()
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    fun loadFacultyFromFirebase(){
        val listItems = ArrayList<Faculty>()

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        reference.child("Faculty").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listItems.clear()
                try{
                    snapshot.children.forEach {
                        val name = it.child("name").value.toString()
                        val score = it.child("score").value.toString()
                        val dataFaculty = Faculty(
                            name, score
                        )
                        listItems.add(dataFaculty)
                    }
                    Log.d("Sebelum sort", listItems.toString())
                    val sortedList = sortDataLeaderboard(listItems)
                    Log.d("Setelah sort", sortedList.toString())
                    listLeaderboard.postValue(sortedList as ArrayList<Faculty>?)
                }catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })
    }

    private fun sortDataLeaderboard(listItems: ArrayList<Faculty>): List<Faculty> {
        return listItems.sortedByDescending {
            it.score.toInt()
        }.toList()
    }

    fun getListFacultyLeaderboard() : LiveData<ArrayList<Faculty>> {
        return listLeaderboard
    }

}