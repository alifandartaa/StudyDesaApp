package com.example.studydesaapp.ui.ui.listmap

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.FragmentLocationMapBinding
import com.example.studydesaapp.ui.entity.LocationEntity
import com.example.studydesaapp.ui.entity.MarkEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LocationMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationMapBinding: FragmentLocationMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationMapBinding = FragmentLocationMapBinding.inflate(layoutInflater, container, false)
        return locationMapBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_location_list) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        val locationMapViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LocationMapViewModel::class.java)

        locationMapViewModel.loadMapLocationFromFirebase()

        locationMapViewModel.getDataLocationMap().observe(requireActivity(), { markerItems ->
            if (markerItems != null) {
                for (i in markerItems) {
                    val pos = LatLng(i.latitude.toDouble(), i.longitude.toDouble())
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(i.name)
                    )
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f))
                }
            }
        })
    }
}