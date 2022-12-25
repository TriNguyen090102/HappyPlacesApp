package com.happyplaces

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.happyplaces.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // TODO (Step 1: Adding an click event to Fab button and calling the AddHappyPlaceActivity.)
        // START
        // Setting an click event for Fab Button and calling the AddHappyPlaceActivity.
        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        // END
    }
}