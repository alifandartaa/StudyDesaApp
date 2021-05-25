package com.example.studydesaapp.ui.ui.listlocation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studydesaapp.ui.entity.LocationEntity
import com.example.studydesaapp.ui.entity.MarkEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception

class LocationListViewModel : ViewModel() {
    private val listLocation = MutableLiveData<ArrayList<LocationEntity>>()
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    var faculty = ""

    fun loadListLocationFromFirebase() {
        val listItems = ArrayList<LocationEntity>()

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        reference.child("Location").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listItems.clear()
                try {
                    snapshot.children.forEach {
                        val name = it.child("name").value.toString()
                        val descrole = it.child("descrole").value.toString()
                        val infoLocation = it.child("info_location").value.toString()
                        val latitude = it.child("latitude").value.toString()
                        val longitude = it.child("longitude").value.toString()
                        val phone = it.child("phone").value.toString()
                        val photo = it.child("photo").value.toString()
                        val dataLocation = LocationEntity(
                            name, descrole, infoLocation, latitude, longitude, phone, photo
                        )
                        listItems.add(dataLocation)
                    }
                    listLocation.postValue(listItems)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })
    }

    fun getListDataLocation(): LiveData<ArrayList<LocationEntity>> {
        return listLocation
    }
}