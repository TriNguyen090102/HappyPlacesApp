package com.happyplaces.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyplaces.databinding.ItemHappyPlaceBinding
import com.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(
    private val context: Context,
    private val items: ArrayList<HappyPlaceModel>) :
    RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {
    private var listener: OnItemClickListener? = null

    class ViewHolder(binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeImage = binding.ivPlaceImage
        val placeName = binding.tvName
        val description = binding.tvDescription
        // Inside the ViewHolder class

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val happyPlace = items.get(position)
        holder.placeImage.setImageURI(Uri.parse(happyPlace.image))
        holder.placeName.text = happyPlace.title
        holder.description.text = happyPlace.description
        holder.itemView.setOnClickListener {
            listener!!.onItemClick(happyPlace, position)
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, items[position])
        activity.startActivityForResult(
            intent,
            requestCode
        ) // Activity is started with requestCode

        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(model: HappyPlaceModel, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}