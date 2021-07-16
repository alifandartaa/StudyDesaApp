package com.example.studydesaapp.ui.ui.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.ActivityDetailLocationBinding
import com.example.studydesaapp.ui.entity.LocationEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.math.RoundingMode

class DetailLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_LOCATION = "EXTRA_LOCATION"
        const val earthRadiusKm: Double = 6372.8
    }

    private lateinit var detailLocationBinding: ActivityDetailLocationBinding
    var gMap: GoogleMap? = null
    var polyline: Polyline? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailLocationBinding = ActivityDetailLocationBinding.inflate(layoutInflater)
        setContentView(detailLocationBinding.root)

        supportActionBar?.title = "Detail Lokasi Desa KKN"

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_distance_ub) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        gMap = googleMap
        if (checkLocationPermission()) {
            val detailLocation = intent.getParcelableExtra<LocationEntity>(EXTRA_LOCATION)
            detailLocationBinding.btnShowVillageImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(detailLocation!!.photo))
                startActivity(intent)
            }

            val ubLat = -7.9525
            val ubLong = 112.6139
            val ubPos = LatLng(ubLat, ubLong)
            val pos =
                LatLng(detailLocation!!.latitude.toDouble(), detailLocation.longitude.toDouble())

            val estimationDistance = "Estimasi jarak yaitu " + haversine(pos, ubPos) + " km"
            gMap?.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(detailLocation.name)
                    .snippet(estimationDistance)
            )
            gMap?.moveCamera(CameraUpdateFactory.newLatLng(pos))
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 9f))
            val latLngList = ArrayList<LatLng>()
            latLngList.add(pos)
            latLngList.add(ubPos)
            if (polyline != null) polyline?.remove()
            val polylineOptions = PolylineOptions()
                .addAll(latLngList)
                .clickable(true)
            polyline = gMap?.addPolyline(polylineOptions)
            polyline!!.color = R.color.colorPrimary

            gMap?.addMarker(
                MarkerOptions()
                    .position(ubPos)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Universitas Brawijaya")
                    .snippet(estimationDistance)
            )

            detailLocationBinding.tvValueDetailName.text = detailLocation.name
            detailLocationBinding.tvValueDetailRole.text = detailLocation.descrole
            detailLocationBinding.tvValueProblem.text = detailLocation.problem
            detailLocationBinding.tvValueSector.text = detailLocation.sector
            detailLocationBinding.tvValueWorkActivities.text = detailLocation.work_activities
            detailLocationBinding.tvValueTourist.text = detailLocation.tourist_site
            detailLocationBinding.tvValueDateAdd.text = detailLocation.currentdate
            detailLocationBinding.tvValueContact.text = detailLocation.phone

        }
    }

    private fun checkLocationPermission(): Boolean {
        var state = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && this.checkSelfPermission(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                state = true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1000
                )
            }
        }
        return state
    }

    fun haversine(source: LatLng, destination: LatLng): Double {
        val dLat = Math.toRadians(destination.latitude - source.latitude);
        val dLon = Math.toRadians(destination.longitude - source.longitude);
        val originLat = Math.toRadians(source.latitude);
        val destinationLat = Math.toRadians(destination.latitude);

        val a = Math.pow(Math.sin(dLat / 2), 2.toDouble()) + Math.pow(
            Math.sin(dLon / 2),
            2.toDouble()
        ) * Math.cos(originLat) * Math.cos(destinationLat);
        val c = 2 * Math.asin(Math.sqrt(a));
        val result = earthRadiusKm * c
        return result.toBigDecimal().setScale(1, RoundingMode.UP).toDouble();
    }
}

