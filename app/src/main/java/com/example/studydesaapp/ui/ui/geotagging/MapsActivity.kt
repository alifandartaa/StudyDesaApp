package com.example.studydesaapp.ui.ui.geotagging

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    var currentMarker: Marker? = null
    lateinit var newLatLng: LatLng
    private lateinit var mMap: GoogleMap
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0


    companion object {
        private const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        private const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
        private const val EXTRA_NEW_LAT = "EXTRA_NEW_LAT"
        private const val EXTRA_NEW_LNG = "EXTRA_NEW_LNG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapActivityBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapActivityBinding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        supportActionBar?.title = "Tentukan Lokasi Desa KKN"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapActivityBinding.fabSendNewLocation.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setMessage(resources.getString(R.string.msg_change_location))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    val intent = Intent(this, AddLocationActivity::class.java)
                    intent.putExtra(EXTRA_NEW_LAT, newLatLng.latitude)
                    intent.putExtra(EXTRA_NEW_LNG, newLatLng.longitude)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                .show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        currentLatitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
        currentLongitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
        // Add a marker in Sydney and move the camera
        val lastPosition = LatLng(currentLatitude, currentLongitude)
        drawMarker(lastPosition)

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }

            override fun onMarkerDragEnd(p0: Marker?) {
                if (currentMarker != null)
                    currentMarker?.remove()

                newLatLng = p0!!.position
                drawMarker(newLatLng)
            }
        })
    }

    private fun drawMarker(
        lastPosition: LatLng,
    ) {
        val markerOptions = MarkerOptions()
            .position(lastPosition)
            .title("Lokasi desa yang dipilih")
            .snippet(getAddress(lastPosition.latitude, lastPosition.longitude))
            .draggable(true)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastPosition))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 15f))
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun getAddress(currentLatitude: Double, currentLongitude: Double): String? {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(currentLatitude, currentLongitude, 1)
        return addresses[0].getAddressLine(0).toString()
    }
}