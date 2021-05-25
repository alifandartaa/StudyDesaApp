package com.example.studydesaapp.ui.ui.listlocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studydesaapp.databinding.FragmentLocationListBinding
import com.example.studydesaapp.ui.ui.geotagging.AddLocationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LocationListFragment : Fragment() {

    private lateinit var locationListBinding: FragmentLocationListBinding
    private lateinit var locationAdapter: LocationAdapter
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationListBinding = FragmentLocationListBinding.inflate(layoutInflater, container, false)
        return locationListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
                LocationListViewModel::class.java
            )
            locationAdapter = LocationAdapter()
            setupListLocation(viewModel)

            firebaseAuth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            reference = database.getReference()

            locationListBinding.fabLocation.setOnClickListener {
                val intent = Intent(context, AddLocationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupListLocation(viewModel: LocationListViewModel) {
        showLoading(true)

        viewModel.loadListLocationFromFirebase()

        viewModel.getListDataLocation().observe(requireActivity(), { locationItems ->
            if (locationItems != null) {
                locationAdapter.setLocation(locationItems)
                showLoading(false)
                locationAdapter.notifyDataSetChanged()
            }
            if(locationItems.isEmpty()){
                locationListBinding.tvEmptyLocation.visibility = View.VISIBLE
            }else{
                locationListBinding.tvEmptyLocation.visibility = View.GONE
            }
        })


        with(locationListBinding.rvLocation) {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = locationAdapter
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            locationListBinding.progressBar.visibility = View.VISIBLE
        } else {
            locationListBinding.progressBar.visibility = View.GONE
        }
    }

}