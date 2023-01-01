package com.happyplaces.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.happyplaces.Utils.SwipeToEditCallback
import com.happyplaces.database.DatabaseHandler
import com.happyplaces.databinding.ActivityMainBinding
import com.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var happyPlaceAdapter: HappyPlaceAdapter? = null

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 0
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        // This is used to align the xml view to this class
        setContentView(binding?.root)

        // Setting an click event for Fab Button and calling the AddHappyPlaceActivity.
        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
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

    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>) {
        if (happyPlacesList.size != 0) {

            binding!!.tvNoHappyPlacesFound.visibility = View.GONE
            binding!!.rvHappyplaces.visibility = View.VISIBLE
            happyPlaceAdapter = HappyPlaceAdapter(this@MainActivity,happyPlacesList)
            binding!!.rvHappyplaces.adapter = happyPlaceAdapter
            happyPlaceAdapter!!.setOnItemClickListener(object :
                HappyPlaceAdapter.OnItemClickListener {

                override fun onItemClick(model: HappyPlaceModel, position: Int) {
                    val intent = Intent(this@MainActivity, HappyPlaceDetail::class.java)
                    intent.putExtra("happyPlaceDetail", model)
                    startActivity(intent)
                }
            })
            val editSwipeHandler = object : SwipeToEditCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = binding!!.rvHappyplaces.adapter as HappyPlaceAdapter
                    adapter.notifyEditItem(
                        this@MainActivity,
                        viewHolder.adapterPosition,
                        ADD_PLACE_ACTIVITY_REQUEST_CODE
                    )
                }

            }
            val itemTouchHelper = ItemTouchHelper(editSwipeHandler)
            itemTouchHelper.attachToRecyclerView(binding!!.rvHappyplaces)
            happyPlaceAdapter!!.notifyDataSetChanged()

        } else {
            binding!!.tvNoHappyPlacesFound.visibility = View.VISIBLE
            binding!!.rvHappyplaces.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val happyPlacesList = getHappyPlacesListFromDatabase()
                setupHappyPlacesRecyclerView(happyPlacesList)
            }
        }
    }
}