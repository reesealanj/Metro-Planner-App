package com.example.daytripplanner_project1

import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync

class DetailsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        Log.d("DetailsActivity", "1")
        val currAddress: Address? = intent.getParcelableExtra("address")
        Log.d("DetailsActivity", "2")
        val foodCat = intent.getStringExtra("foodType")
        Log.d("DetailsActivity", "3")
        val attrCat = intent.getStringExtra("attrType")
        val foodRes = intent.getIntExtra("foodRes", 1)
        val attrRes = intent.getIntExtra("attrRes", 1)
        val addrText = currAddress?.getAddressLine(0).toString()

        Log.d("DetailsActivity", "CurrAddress: $addrText")
        Log.d("DetailsActivity", "foodCat: $foodCat")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        doAsync {
            val manager = DetailManager()
            val yelpKey = getString(R.string.yelp_api_key)

            try {
                val places = manager.retrieveDetails(
                    foodCat,
                    attrCat,
                    currAddress!!.latitude,
                    currAddress!!.longitude,
                    foodRes,
                    attrRes,
                    yelpKey
                )
                runOnUiThread {
                    val adapter = DetailAdapter(places)
                    recyclerView.adapter = adapter
                }
            } catch (exception: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@DetailsActivity,
                        "Failed to Retrieve location information",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
