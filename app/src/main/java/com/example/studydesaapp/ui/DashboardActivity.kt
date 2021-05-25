package com.example.studydesaapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.ActivityDashboardBinding
import com.example.studydesaapp.ui.ui.listlocation.LocationListFragment
import com.example.studydesaapp.ui.ui.listmap.LocationMapFragment
import com.example.studydesaapp.ui.ui.profile.ProfileFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire

class DashboardActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private val fragmentListLocation = LocationListFragment()
    private val fragmentMapLocation = LocationMapFragment()
    private val fragmentProfile = ProfileFragment()
    private val fm = supportFragmentManager
    private var activeFragment = Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        activeFragment = fragmentListLocation
        supportActionBar?.hide()
        navView.setOnNavigationItemSelectedListener(this)
        fm.beginTransaction().add(R.id.dashboard_container, fragmentProfile, "Profile")
            .hide(fragmentProfile).commit()
        fm.beginTransaction().add(R.id.dashboard_container, fragmentMapLocation, "Map")
            .hide(fragmentMapLocation).commit()
        fm.beginTransaction().add(R.id.dashboard_container, fragmentListLocation, "List").commit()

        NoInternetDialogPendulum.Builder(
            this,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {

                    }
                }
                cancelable = false // Optional
                noInternetConnectionTitle = "Tidak ada koneksi" // Optional
                noInternetConnectionMessage =
                    "Periksa koneksi internetmu dan coba kembali" // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Tolong aktifkan" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_location_list) {
            fm.beginTransaction().hide(activeFragment).show(fragmentListLocation).commit();
            activeFragment = fragmentListLocation
            supportActionBar?.hide()
            return true
        } else if (item.itemId == R.id.navigation_map_list) {
            fm.beginTransaction().hide(activeFragment).show(fragmentMapLocation).commit();
            activeFragment = fragmentMapLocation
            supportActionBar?.hide()
            return true
        } else if(item.itemId == R.id.navigation_profile) {
            fm.beginTransaction().hide(activeFragment).show(fragmentProfile).commit();
            activeFragment = fragmentProfile
            supportActionBar?.hide()
            return true
        }else{
            return false
    }
}
}