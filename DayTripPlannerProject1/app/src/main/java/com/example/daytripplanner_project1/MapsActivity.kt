package com.example.daytripplanner_project1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var submit: Button
    private lateinit var goBack: Button
    private lateinit var currAddress: Address
    private lateinit var foodCat: String
    private lateinit var attrCat: String
    private lateinit var progress: ProgressBar
    private var foodRes: Int = 0
    private var attrRes: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        currAddress = intent.getParcelableExtra("address")
        foodCat = intent.getStringExtra("foodType")
        attrCat = intent.getStringExtra("attrType")
        foodRes = intent.getIntExtra("foodRes", 1)
        attrRes = intent.getIntExtra("attrRes", 1)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        submit = findViewById(R.id.confirm)

        submit.setOnClickListener {view: View ->
            val intent: Intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("address", currAddress)
            intent.putExtra("foodType", foodCat)
            intent.putExtra("attrType", attrCat)
            intent.putExtra("foodRes", foodRes)
            intent.putExtra("attrRes", attrRes)
            startActivity(intent)
        }

        goBack = findViewById(R.id.goBackButton)

        goBack.setOnClickListener { view: View ->
            val intent: Intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        progress = findViewById(R.id.progressBar)
        progress.visibility = View.GONE
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
        val geocoder = Geocoder(this)
        val result = geocoder.getFromLocationName(currAddress.getAddressLine(0).toString(), 1)
        val latLng = LatLng(result.first().latitude, result.first().longitude)
        mMap.addMarker(
            MarkerOptions().position(latLng).title(currAddress.getAddressLine(0).toString())
        )
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 13.8f)
        )
        mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(1500.0)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT)
        )
        progress.visibility = View.VISIBLE
        submit.isEnabled = false
        yelpMarkers(latLng)
        wmataMarkers(latLng)
        submit.isEnabled = true
        progress.visibility = View.GONE
    }

    fun wmataMarkers(latLng: LatLng) {
        doAsync {
            val manager = WMATAManager()
            val wmataKey = getString(R.string.wmata_pri_key)

            try  {
                val station = manager.wamataRetrieval(latLng, wmataKey)
                runOnUiThread{
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(station.latitude, station.longitude))
                            .title(station.name)
                            .icon(BitmapDescriptorFactory.defaultMarker(304F))
                    )
                }
            } catch (exception: Exception){
                runOnUiThread {
                    Log.e("MapsActivity", exception.toString())
                    Toast.makeText(this@MapsActivity, "Failed to Closest Station information", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun yelpMarkers(latLng: LatLng) {

        val geocoder = Geocoder(this)

        doAsync {
            val manager = DetailManager()
            val yelpKey = getString(R.string.yelp_api_key)

            try {
                val places = manager.retrieveDetails(foodCat, attrCat, latLng.latitude, latLng.longitude, foodRes, attrRes, yelpKey)
                runOnUiThread {
                    val pIterator = places.iterator()
                    pIterator.forEach {result: Detail ->
                        val addr = "${result.address} ${result.address2}"
                        val location = geocoder.getFromLocationName(addr, 1)
                        if(result.type == 1){
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(location.first().latitude, location.first().longitude))
                                    .title(result.name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(107F))
                            )
                            Log.d("MapsActivity", "Placed Food Marker - ${result.name}")
                        } else {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(location.first().latitude, location.first().longitude))
                                    .title(result.name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(20F))
                            )
                            Log.d("MapsActivity", "Placed Attraction Marker - ${result.name}")
                        }
                    }
                }

            } catch (exception: Exception) {
                runOnUiThread {
                    Log.e("MapsActivity", exception.toString())
                    Toast.makeText(this@MapsActivity, "Failed to Retrieve location information", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
