package com.example.daytripplanner_project1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.doAsync
import kotlin.math.log

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var destination: EditText
    private lateinit var food_cat: Spinner
    private lateinit var food_res: SeekBar
    private lateinit var attr_cat: Spinner
    private lateinit var attr_res: SeekBar
    private lateinit var submit: Button
    private lateinit var progress: ProgressBar
    private lateinit var prefs: SharedPreferences
    private lateinit var food_res_num: TextView
    private lateinit var attr_res_num: TextView

    private var dest: Boolean = false
    private var food: Boolean = false
    private var attr: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Designate the layout to be used
        setContentView(R.layout.activity_main)
        // Bind IDs to variables
        destination = findViewById(R.id.destInput)
        food_cat = findViewById(R.id.foodSpinner)
        food_res = findViewById(R.id.foodSeekBar)
        attr_cat = findViewById(R.id.attrSpinner)
        attr_res = findViewById(R.id.attrSeekBar)
        submit = findViewById(R.id.submitButton)
        progress = findViewById(R.id.homeProgressBar)
        food_res_num = findViewById(R.id.foodResultsLabel)
        attr_res_num = findViewById(R.id.attrResultsLabel)

        // Set up the onClickListener for the Submit button
        submit.setOnClickListener { view: View ->
            Log.d("HomeScreenActivity", "onClick() Called")
            locationCheck()
        }

        // Begin application with the login button disabled
        enableButton()

        // Bind textWatcher to Destination editText
        destination.addTextChangedListener(textWatcher)
        // Populate the spinners for the Food/Attractions
        // Fill out for Food
        ArrayAdapter.createFromResource(
            this,
            R.array.food_choices,
            android.R.layout.simple_spinner_item
        ).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            food_cat.adapter = adapter
        }
        // Fill out for Attractions
        ArrayAdapter.createFromResource(
            this,
            R.array.attr_choices,
            android.R.layout.simple_spinner_item
        ).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            attr_cat.adapter = adapter
        }

        //Create on Seek Bar Changed Listeners for SeekBars
        food_res.setOnSeekBarChangeListener(foodSeek)
        attr_res.setOnSeekBarChangeListener(attrSeek)
        //Set text fields with 0 progress on creation
        food_res_num.text = getString(R.string.food_results, 0)
        attr_res_num.text = getString(R.string.attr_results, 0)
        //Create on Item Selected listeners for spinners
        food_cat.onItemSelectedListener = foodWatcher
        attr_cat.onItemSelectedListener = attrWatcher

        //Initialize preferences
        prefs = getSharedPreferences(R.string.pref_file_name.toString(), Context.MODE_PRIVATE)
        // Load previously saved information into variables
        var savedFood: Int = prefs.getInt(R.string.food_saved_selection.toString(), 0)
        var savedAttr: Int = prefs.getInt(R.string.attr_saved_selection.toString(), 0)
        var savedFoodSeek: Int = prefs.getInt(R.string.food_seekBar.toString(), 0)
        var savedAttrSeek: Int = prefs.getInt(R.string.attr_seekBar.toString(), 0)
        var savedDestText: String? = prefs.getString(R.string.dest_saved_content.toString(), "")
        // Transfer saved information into the application
        food_res.progress = savedFoodSeek
        attr_res.progress = savedAttrSeek
        attr_res_num.text = getString(R.string.attr_results, attr_res.progress.toString())
        food_res_num.text = getString(R.string.food_results, food_res.progress.toString())
        food_cat.setSelection(savedFood)
        attr_cat.setSelection(savedAttr)
        destination.setText(savedDestText)
        // Ensure enableButton is called at least once after
        enableButton()
    }

    // Implementation of TextWatcher for the Destination input text
    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable) { }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(destination.text.toString().trim().isNotEmpty()) {
                prefs.edit().putString(R.string.dest_saved_content.toString(), destination.text.toString().trim())
                    .apply()
                dest = true
            } else {
                dest = false
            }
            enableButton()
        }
    }

    // Implementations of OnItemSelectedListener for Spinners
    private val foodWatcher = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) { }

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if(position > 0) {
                food = true
                prefs.edit()
                    .putInt(R.string.food_saved_selection.toString(), position)
                    .putString(R.string.food_saved_content.toString(), parent?.getItemAtPosition(position).toString())
                    .apply()
            } else {
                food = false
            }
            enableButton()
        }
    }
    // Implementation of listener for Attraction Spinner
    private val attrWatcher = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) { }

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (position > 0) {
                attr = true
                prefs.edit()
                    .putInt(R.string.attr_saved_selection.toString(), position)
                    .putString(R.string.attr_saved_content.toString(), parent?.getItemAtPosition(position).toString())
                    .apply()
                enableButton()
            } else {
                attr = false
            }
            enableButton()
        }
    }
    // Implementation of listener for Food SeekBar
    private val foodSeek = object: SeekBar.OnSeekBarChangeListener {

        var seekVal = 0

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            seekVal = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) { }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            prefs.edit()
                .putInt(R.string.food_seekBar.toString(), seekVal)
                .apply()
            food_res_num.text = getString(R.string.food_results, seekVal.toString())
        }
    }
    // Implementation of listener for Attractions SeekBar
    private val attrSeek = object: SeekBar.OnSeekBarChangeListener {

        var seekVal = 0

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            seekVal = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) { }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            prefs.edit()
                .putInt(R.string.attr_seekBar.toString(), seekVal)
                .apply()
            attr_res_num.text = getString(R.string.attr_results, seekVal.toString())
        }
    }

    // Function to check whether or not the submit button should be enabled
    fun enableButton() {
        submit.isEnabled = food && attr && dest
    }

    fun locationCheck() {

        doAsync{
            val geocoder = Geocoder(this@HomeScreenActivity)

            val results: List<Address> = try {
                geocoder.getFromLocationName(destination.text.toString().trim(), 10)
            } catch (exception: Exception){
                exception.printStackTrace()
                Log.e("HomeScreenActivity", "Failed to retrieve Geocoding results: $exception")
                listOf<Address>()
            }

            if(results.isNotEmpty()) {
                Log.d("HomeScreenActivity", "Recieved ${results.size} Geocoding results")

                val arrayAdapter = ArrayAdapter<String>(this@HomeScreenActivity, android.R.layout.select_dialog_singlechoice)
                val iterator = results.iterator()
                iterator.forEach {
                    result: Address ->
                    arrayAdapter.add(result.getAddressLine(0).toString())
                }

                runOnUiThread {
                    AlertDialog.Builder(this@HomeScreenActivity)
                        .setTitle("Search Results")
                        .setAdapter(arrayAdapter) { dialog, which ->

                            val intent: Intent = Intent(this@HomeScreenActivity, MapsActivity::class.java)
                                intent.putExtra("address", results[which])
                                intent.putExtra("foodType", prefs.getString(R.string.food_saved_content.toString(), ""))
                                intent.putExtra("attrType", prefs.getString(R.string.attr_saved_content.toString(), ""))
                                intent.putExtra("foodRes", foodSeek.seekVal)
                                intent.putExtra("attrRes", attrSeek.seekVal)
                                startActivity(intent)
                        }
                        .setNegativeButton("Cancel") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
            else {
                runOnUiThread {
                    Toast.makeText( this@HomeScreenActivity, "Failed to fetch Geocoded Address", Toast.LENGTH_LONG).show()
                }

            }
        }
    }

}
