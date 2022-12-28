package com.happyplaces.activities

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.happyplaces.databinding.ItemHappyPlaceBinding
import com.happyplaces.models.HappyPlaceModel

class HappyPlaceAdapter(private val items : ArrayList<HappyPlaceModel>) : RecyclerView.Adapter<HappyPlaceAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val placeImage = binding.ivPlaceImage
        val placeName = binding.tvName
        val description = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val happyPlace = items.get(position)
        holder.placeImage.setImageDrawable(BitmapDrawable(Resources.getSystem(), happyPlace.image))
        holder.placeName.text = happyPlace.title
        holder.description.text = happyPlace.description
    }

    override fun getItemCount(): Int {
        return items.size
    }
}