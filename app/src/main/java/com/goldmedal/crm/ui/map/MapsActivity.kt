package com.goldmedal.hrapp.ui.map
//
//import kotlinx.android.synthetic.main.activity_maps.*

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the content view that renders the map.
//        setContentView(R.layout.activity_maps)
//        if (getString(R.string.maps_api_key).isEmpty()) {
//            Toast.makeText(this, "Add your own API key in MapWithMarker/app/secure.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
//        }

        // Get the SupportMapFragment and request notification when the map is ready to be used.
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
//        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            val sydney = LatLng(-33.852, 151.211)
            addMarker(
                    MarkerOptions()
                            .position(sydney)
                            .title("Marker in Sydney")
            )
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }
}