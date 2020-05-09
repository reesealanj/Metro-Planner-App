package com.example.daytripplanner_project1

import android.os.Bundle
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.*

class DetailManager {

    private val okHttpClient: OkHttpClient
    init {
        val builder = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        
        builder.addInterceptor(loggingInterceptor)
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)
        
        okHttpClient = builder.build()
    }

    fun retrieveDetails(food: String, attr: String, latitude: Double, longitude: Double, fResult: Int, aResult: Int, apiKey: String): List<Detail> {
        Log.d("DetailManager", "Starting Yelp requests for $food and $attr")
        val requestFood = Request.Builder()
            .url("https://api.yelp.com/v3/businesses/search?term=${food}&categories=${food}&latitude=${latitude}&longitude=${longitude}&radius=1500&limit=${fResult}")
            .header("Authorization", "Bearer $apiKey")
            .build()

        val requestAttr = Request.Builder()
            .url("https://api.yelp.com/v3/businesses/search?term=${attr}&categories=${attr}&latitude=${latitude}&longitude=${longitude}&radius=1500&limit=${aResult}")
            .header("Authorization", "Bearer $apiKey")
            .build()

        Log.d("DetailManager", "Sent food request")
        val foodResponse = okHttpClient.newCall(requestFood).execute()
        Log.d("DetailManager", "End food request")
        Log.d("DetailManager", "Sent attr request")
        val attrResponse = okHttpClient.newCall(requestAttr).execute()
        Log.d("DetailManager", "End attr request")

        val details: MutableList<Detail> = mutableListOf()

        val foodString: String? = foodResponse.body?.string()
        Log.d("DetailManager", "Food Response Body Grabbed")
        val attrString: String? = attrResponse.body?.string()
        Log.d("DetailManager", "Attraction Response Body Grabbed")

        if (foodResponse.isSuccessful && attrResponse.isSuccessful) {
            // Parse the food request responses
            val foodJSON = JSONObject(foodString)
            Log.d("DetailManager", "Food JSONObject Fine")
            val foodPlaces: JSONArray = foodJSON.getJSONArray("businesses")
            Log.d("DetailManager", "Food JSONArray Fine")
            for(i in 0 until foodPlaces.length()) {
                Log.d("DetailManager", "Food Post $i")
                val curr = foodPlaces.getJSONObject(i)
                val name = curr.getString("name")
                var pricePoint: String = if(curr.has("price")) {
                    curr.getString("price")
                } else {
                    "None"
                }
                val phone: String = if(curr.has("phone")){
                    curr.getString("phone")
                } else {
                    ""
                }
                val url: String = if(curr.has("url")){
                    curr.getString("url")
                } else {
                    ""
                }

                val rating = curr.getInt("rating")

                val addrObj = curr.getJSONObject("location")
                val street: String = addrObj.getString("address1")
                val city: String = addrObj.getString("city")
                val zip: String = addrObj.getString("zip_code")
                val state: String = addrObj.getString("state")
                val addrString = "$city, $state $zip"

                val detail = Detail(
                    name = name,
                    pricePoint = pricePoint,
                    rating = rating,
                    address = street,
                    address2 = addrString,
                    phone = phone,
                    url = url,
                    type = 1
                )

                details.add(detail)
            }

            // Parse the attraction request responses
            val attrJSON = JSONObject(attrString)
            Log.d("DetailManager", "Attraction JSONObject Fine")
            val attrPlaces: JSONArray = attrJSON.getJSONArray("businesses")
            Log.d("DetailManager", "Attraction JSONArray Fine")
            for(i in 0 until attrPlaces.length()) {
                Log.d("DetailManager", "Attraction Post $i")
                val curr = attrPlaces.getJSONObject(i)
                val name = curr.getString("name")
                val pricePoint: String = if(curr.has("price")) {
                    curr.getString("price")
                } else {
                    "None"
                }
                val phone: String = if(curr.has("phone")){
                    curr.getString("phone")
                } else {
                    ""
                }
                val url: String = if(curr.has("url")){
                    curr.getString("url")
                } else {
                    ""
                }

                val rating = curr.getInt("rating")

                val addrObj = curr.getJSONObject("location")
                val street: String = addrObj.getString("address1")
                val city: String = addrObj.getString("city")
                val zip: String = addrObj.getString("zip_code")
                val state: String = addrObj.getString("state")
                val addrString = "$city, $state $zip"

                val detail = Detail(
                    name = name,
                    pricePoint = pricePoint,
                    rating = rating,
                    address = street,
                    address2 = addrString,
                    phone = phone,
                    url = url,
                    type = 0
                )

                details.add(detail)
            }
        }

        return details.sortedByDescending {it.rating}
    }
}