package com.happyplaces.models

import android.app.DatePickerDialog
import android.graphics.Bitmap
import java.io.Serializable

data class HappyPlaceModel(
    val id : Int,
    val title : String,
    val image : String,
    val description : String,
    val date : String,
    val location : String,
    val latitude : Double,
    val longtitude: Double
) : Serializable
