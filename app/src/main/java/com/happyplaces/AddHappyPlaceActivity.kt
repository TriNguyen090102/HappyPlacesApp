package com.happyplaces

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity() {

    private var binding: ActivityAddHappyPlaceBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // TODO (Step 2: Setting up the action bar using the toolbar and making enable the home back button and also adding the click of it.)
        // START
        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        toolbar_add_place.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.etDate?.setOnClickListener {
            setupDatePickerDialog()
        }
    }

    private fun setupDatePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Update the EditText with the selected date
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding?.etDate?.setText(dateFormat.format(selectedDate.time))
        }

        DatePickerDialog(this@AddHappyPlaceActivity,dpd,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}