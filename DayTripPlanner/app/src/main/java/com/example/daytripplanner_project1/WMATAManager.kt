package com.example.daytripplanner_project1

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WMATAManager {

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

    fun wamataRetrieval(location: LatLng, apiKey: String): Station {
        val request = Request.Builder()
            .url("https://api.wmata.com/Rail.svc/json/jStationEntrances?lat=${location.latitude}&lon=${location.longitude}&radius=1500")
            .header("api_key", apiKey)
            .build()

        val response = okHttpClient.newCall(request).execute()
        val resString: String? = response.body?.string()
        var codeOut = ""


        if(response.isSuccessful) {
            val json = JSONObject(resString)
            val entrances: JSONArray = json.getJSONArray("Entrances")
            val closest = entrances.getJSONObject(0)
            codeOut = closest.getString("StationCode1")
        }

        val request2 = Request.Builder()
            .url("https://api.wmata.com/Rail.svc/json/jStationInfo?StationCode=${codeOut}")
            .header("api_key", apiKey)
            .build()

        val response2 = okHttpClient.newCall(request2).execute()
        val resString2: String? = response2.body?.string()
        var nameOut = ""
        var lat: Double = 0.0
        var lon: Double = 0.0

        if(response2.isSuccessful){
            val json = JSONObject(resString2)
            nameOut = json.getString("Name")
            lat = json.getDouble("Lat")
            lon = json.getDouble("Lon")
        }

        return Station(nameOut, lat, lon, codeOut)
    }
}