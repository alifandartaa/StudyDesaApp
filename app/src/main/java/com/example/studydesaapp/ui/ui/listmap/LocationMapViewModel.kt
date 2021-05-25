package com.example.studydesaapp.ui.ui.listmap

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studydesaapp.ui.entity.MarkEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class LocationMapViewModel : ViewModel() {
    private val listLocationMap = MutableLiveData<ArrayList<MarkEntity>>()
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    fun loadMapLocationFromFirebase(){
        val listItems = ArrayList<MarkEntity>()

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        reference.child("Location").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    snapshot.children.forEach {
                        val name = it.child("name").value.toString()
                        val latitude = it.child("latitude").value.toString()
                        val longitude = it.child("longitude").value.toString()
                        val dataLocation = MarkEntity(
                            name, latitude, longitude
                        )
                        listItems.add(dataLocation)
                    }
                    listLocationMap.postValue(listItems)
                }catch (e:Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })

    }

    fun getDataLocationMap() : LiveData<ArrayList<MarkEntity>>{
        return listLocationMap
    }
}