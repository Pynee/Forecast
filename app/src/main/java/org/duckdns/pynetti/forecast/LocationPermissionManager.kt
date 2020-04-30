package org.duckdns.pynetti.forecast

/**
 * LocationPermissionManager
 * Manages users location permissions and location information
 *
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

/**
(LocationManager.GPS_PROVIDER).apply {
latitude = 61.49911
longitude = 23.78712
}*/


class LocationPermissionManager(
    private var mainActivity: MainActivity,
    val callback: (LatLng) -> Unit
) : FragmentActivity() {
    private val PERMISSION_ID = 909
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var location: Location? = null
    var userSelectLocation: LatLng? = null
    var lastMapLocation: LatLng? = null
    var userLocation: LatLng? = null //= LatLng(61.49911, 23.78712)

    /*
    class CustomLocationListener(provider: String) : android.location.LocationListener {

        override fun onLocationChanged(location: Location?) {
            latlng = LatLng(location!!.latitude,location.longitude)
            callback(latlng!!)
        }

        override fun onProviderDisabled(provider: String?) {
            Toast.makeText(mainActivity.applicationContext, "You need to enable location services in settings", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mainActivity.startActivity(intent)
        }

        override fun onProviderEnabled(provider: String?) {
            latlng = LatLng(userLocation!!.latitude,userLocation!!.longitude)
            callback(latlng!!)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

    }


    private val locationListener: LocationListener = CustomLocationListener
    */
    fun requestLocation(): Boolean {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        val permissionAccessLocationApproved = ActivityCompat
            .checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat
            .checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val locationManager =
            mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    location = locationResult.lastLocation
                    userLocation = LatLng(location!!.latitude, location!!.longitude)
                    callback(userLocation!!)
                }


            }

        }









        when (permissionAccessLocationApproved) {
            true -> {
                when (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )) {
                    true -> {
                        fusedLocationClient.lastLocation.addOnCompleteListener(mainActivity) { task ->
                            location = task.result
                            if (location == null) {
                                requestNewLocationData()
                            } else {
                                userLocation = LatLng(location!!.latitude, location!!.longitude)
                                callback(userLocation!!)
                            }

                        }

                        return true
                    }
                    false -> {
                        //User doesn't have location services enabled in settings
                        requestLocationSettings()
                        return false
                    }
                }
            }
            false -> {
                // App doesn't have access to the device's location at all. Make full request
                // for permission.
                requestPermissions()
                return false
            }
        }
    }

    private fun requestPermissions() {
        Toast.makeText(
            mainActivity.applicationContext,
            "This app requires location service to work",
            Toast.LENGTH_LONG
        ).show()
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                requestLocation()
            }
        }
    }

    private fun requestLocationSettings() {
        Toast.makeText(
            mainActivity.applicationContext,
            "You need to enable location services in settings",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        mainActivity.startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 100f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }


}