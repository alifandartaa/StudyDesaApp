package com.example.studydesaapp.ui.ui.geotagging

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.ActivityAddLocationBinding
import com.example.studydesaapp.ui.entity.LocationEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.*


class AddLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    var currentMarker: Marker? = null
    lateinit var auth: FirebaseAuth
    lateinit var client: FusedLocationProviderClient
    private var image: ImageView? = null
    private var fileImageUpload: ByteArray? = null
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var changedLatitude: Double? = 0.0
    private var changedLongitude: Double? = 0.0
    private lateinit var mMap: GoogleMap
    private var currentScoreFaculty = ""
    private var currentNameFaculty = ""

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 123
        private const val CHANGE_LOC_REQUEST_CODE = 2000
        private const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        private const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
        private const val EXTRA_NEW_LAT = "EXTRA_NEW_LAT"
        private const val EXTRA_NEW_LNG = "EXTRA_NEW_LNG"
        private const val REQUEST_LOCATION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val addLocationBinding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(addLocationBinding.root)

        supportActionBar?.title = "Geotaging Lokasi Desa KKN"

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        mapFragment.getMapAsync(this)

        image = addLocationBinding.imgLocation
        Glide.with(this)
            .load(R.drawable.no_image_placeholder)
            .apply(RequestOptions().override(400, 300))
            .into(image!!)

        client = LocationServices.getFusedLocationProviderClient(this)
        val storageRef = FirebaseStorage.getInstance().reference

        addLocationBinding.btnCaptureImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Location")

        val viewModelScore = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            AddLocationViewModel::class.java
        )

        viewModelScore.loadScoreFacultyFromFirebase()

        viewModelScore.getScoreFaculty().observe(this, { scoreFaculty ->
            currentScoreFaculty = scoreFaculty
        })

        viewModelScore.getNameFaculty().observe(this, { nameFaculty ->
            currentNameFaculty = nameFaculty
        })

        addLocationBinding.btnChangeLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(EXTRA_LATITUDE, latitude)
            intent.putExtra(EXTRA_LONGITUDE, longitude)
            startActivityForResult(intent, CHANGE_LOC_REQUEST_CODE)
        }

        addLocationBinding.btnSubmitAddLocation.setOnClickListener {
            val filename = UUID.randomUUID().toString()
            val villageImageRef = storageRef.child("images/$filename")
            //check null or not
            if (addLocationBinding.txtVillage.text.toString() == "") {
                addLocationBinding.txtVillage.error = "Harap isi nama desa"
                addLocationBinding.txtVillage.requestFocus()
            } else if (addLocationBinding.txtRole.text.toString() == "") {
                addLocationBinding.txtRole.error = "Harap isi bidang yang dibutuhkan"
                addLocationBinding.txtRole.requestFocus()
            } else if (addLocationBinding.txtProblem.text.toString() == "") {
                addLocationBinding.txtProblem.error = "Harap isi permasalahan desa"
                addLocationBinding.txtProblem.requestFocus()
            } else if (addLocationBinding.txtSector.text.toString() == "") {
                addLocationBinding.txtSector.error = "Harap isi sektor pengembangan desa"
                addLocationBinding.txtSector.requestFocus()
            } else if (addLocationBinding.txtWorkActivities.text.toString() == "") {
                addLocationBinding.txtWorkActivities.error = "Harap isi program kerja desa"
                addLocationBinding.txtWorkActivities.requestFocus()
            } else if (addLocationBinding.txtTouristSite.text.toString() == "") {
                addLocationBinding.txtTouristSite.error = "Harap isi nama tempat wisata"
                addLocationBinding.txtTouristSite.requestFocus()
            } else if (fileImageUpload == null) {
                addLocationBinding.btnCaptureImage.error = "Harap sisipkan foto balai desa"
            } else {
                villageImageRef.putBytes(fileImageUpload!!).addOnSuccessListener {
                    villageImageRef.downloadUrl.addOnSuccessListener {
                        MaterialAlertDialogBuilder(this)
                            .setMessage(resources.getString(R.string.msg_add_location))
                            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                                // Respond to negative button press
                            }
                            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                                addLocation(addLocationBinding, it.toString())
                            }
                            .show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Fail Image Upload", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun addLocation(
        addLocationBinding: ActivityAddLocationBinding,
        photoUrl: String
    ) {
        val date = Calendar.getInstance().time
        val currentDateAndTime: String = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        val location = LocationEntity(
            addLocationBinding.txtVillage.text.toString(),
            currentNameFaculty,
            addLocationBinding.txtRole.text.toString(),
            addLocationBinding.txtProblem.text.toString(),
            addLocationBinding.txtSector.text.toString(),
            addLocationBinding.txtWorkActivities.text.toString(),
            addLocationBinding.txtTouristSite.text.toString(),
            if (changedLatitude == 0.0) latitude.toString() else changedLatitude.toString(),
            if (changedLongitude == 0.0) longitude.toString() else changedLongitude.toString(),
            currentDateAndTime,
            auth.currentUser?.phoneNumber!!,
            photoUrl
        )
        val newPointFaculty = (currentScoreFaculty.toInt() + 10).toString()
        databaseReference!!.child(addLocationBinding.txtVillage.text.toString()).setValue(location)
        database!!.reference.child("Faculty").child(currentNameFaculty).child("score")
            .setValue(newPointFaculty)
        Toast.makeText(this, "Geotaging Lokasi Berhasil", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                onCaptureImageResult(data)
            } else if (requestCode == CHANGE_LOC_REQUEST_CODE) {
                if (currentMarker != null)
                    currentMarker?.remove()
                changedLatitude = data?.getDoubleExtra(EXTRA_NEW_LAT, 0.0)
                changedLongitude = data?.getDoubleExtra(EXTRA_NEW_LNG, 0.0)
                val changedLatLng = LatLng(changedLatitude!!, changedLongitude!!)
                drawMarker(changedLatLng)
            }
        }
    }

    private fun onCaptureImageResult(data: Intent?) {
        val bmp = data?.extras?.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val bb = bytes.toByteArray()
        fileImageUpload = bb
        image?.setImageBitmap(bmp)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )){
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_CODE
                        )
                    })
                    .create()
                    .show()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_CODE
                )
            }
            return
        }
        client.lastLocation.addOnCompleteListener {
            latitude = it.result?.latitude
            longitude = it.result?.longitude

            val pos = LatLng(latitude!!, longitude!!)
            drawMarker(pos)
        }
    }

    private fun drawMarker(pos: LatLng) {
        val markerOptions = MarkerOptions()
            .position(pos)
            .title("Lokasi desa saat ini")
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13f))
        mMap.uiSettings?.isScrollGesturesEnabled = false
        mMap.uiSettings?.isZoomGesturesEnabled = false
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }
}