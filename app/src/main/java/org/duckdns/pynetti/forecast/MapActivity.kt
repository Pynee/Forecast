package org.duckdns.pynetti.forecast

/**
 * MapActivity
 * Shows map so user can select forecast location
 */
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.duckdns.pynetti.forecast.MainActivity.Companion.locationPermissionManager

class MapActivity : BaseActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var userLocationMarker: Marker? = null
    private var userSelectionMarker: Marker? = null
    private var selectionLatLng: LatLng? = locationPermissionManager.userSelectLocation
    private var savedLocation: LatLng? = locationPermissionManager.lastMapLocation
    private var zoom = 12.0f
    private var latLng: LatLng? = null
    private var userLocationMarkerOptions: MarkerOptions? = null
    private var selectionMarkerOptions: MarkerOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0

        //Adding markers to map
        if (locationPermissionManager.userLocation != null) {
            latLng = locationPermissionManager.userLocation
            userLocationMarkerOptions =
                MarkerOptions().position(latLng!!).title("Your Location").icon(
                    BitmapDescriptorFactory.fromBitmap(
                        resources.getDrawable(R.drawable.marker).toBitmap(32, 32)
                    )
                )
        } else {
            latLng = LatLng(61.49911, 23.78712)
        }

        googleMap?.setOnMarkerClickListener { marker ->
            println(marker.title)
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }
        googleMap?.setOnMapClickListener { selectionLatLng ->
            userSelectionMarker?.remove()
            this.selectionLatLng = selectionLatLng
            selectionMarkerOptions =
                MarkerOptions().position(selectionLatLng).title("Your Selection")
                    .snippet("Click to remove").icon(
                        BitmapDescriptorFactory.fromBitmap(
                            resources.getDrawable(R.drawable.pinmarker).toBitmap(32, 32)
                        )
                    )
            locationPermissionManager.userSelectLocation = selectionLatLng
            locationPermissionManager.callback(selectionLatLng)
            googleMap.let {
                userSelectionMarker = it!!.addMarker(selectionMarkerOptions)
                userSelectionMarker!!.showInfoWindow()
            }
        }
        googleMap?.setOnInfoWindowClickListener { marker ->
            if (marker.title != "Your Location") {
                marker.remove()
                locationPermissionManager.callback(
                    locationPermissionManager.userLocation!!
                )
            }
        }
        googleMap?.setOnCameraMoveListener {
            locationPermissionManager.lastMapLocation = googleMap!!.cameraPosition.target
        }

        googleMap.let {
            userLocationMarker?.remove()
            if (userLocationMarkerOptions != null) {
                userLocationMarker = it!!.addMarker(userLocationMarkerOptions)
            }
            if (selectionLatLng != null) {
                selectionMarkerOptions =
                    MarkerOptions().position(selectionLatLng!!).title("Your Selection")
                        .snippet("Click to remove").icon(
                            BitmapDescriptorFactory.fromBitmap(
                                resources.getDrawable(R.drawable.pinmarker).toBitmap(32, 32)
                            )
                        )
                googleMap.let {
                    userSelectionMarker = it!!.addMarker(selectionMarkerOptions)
                    userSelectionMarker!!.showInfoWindow()
                }
            }
            if (savedLocation != null) {
                it!!.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation, zoom))
            } else if (selectionLatLng != null) {
                it!!.moveCamera(CameraUpdateFactory.newLatLngZoom(selectionLatLng, zoom))
            } else {
                it!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        locationPermissionManager.userLocation,
                        zoom
                    )
                )
            }

        }
    }


}
