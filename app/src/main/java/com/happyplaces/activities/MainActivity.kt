package com.happyplaces.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.happyplaces.database.DatabaseHandler
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var happyPlaceAdapter: HappyPlaceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // Setting an click event for Fab Button and calling the AddHappyPlaceActivity.
        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        val happyPlacesList = getHappyPlacesListFromDatabase()
        setupHappyPlacesRecyclerView(happyPlacesList)
        // END
    }

    private fun getHappyPlacesListFromDatabase(): ArrayList<HappyPlaceModel> {
        val dbHandler = DatabaseHandler(this)
        try {
            val happyPlacesList = dbHandler.getHappyPlacesList()
            return happyPlacesList
        } catch (e: Exception) {
            return ArrayList()
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlacesList : ArrayList<HappyPlaceModel>)
    {
        if(happyPlacesList.size != 0)
        {
            binding!!.tvNoHappyPlacesFound.visibility = View.GONE
            binding!!.rvHappyplaces.visibility = View.VISIBLE
            happyPlaceAdapter = HappyPlaceAdapter(happyPlacesList)
            binding!!.rvHappyplaces.adapter = happyPlaceAdapter
            happyPlaceAdapter!!.notifyDataSetChanged()

        } else {
            binding!!.tvNoHappyPlacesFound.visibility = View.VISIBLE
            binding!!.rvHappyplaces.visibility = View.GONE
        }
    }
}