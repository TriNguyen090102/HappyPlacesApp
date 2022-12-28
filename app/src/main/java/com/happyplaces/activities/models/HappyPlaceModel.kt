package com.happyplaces.models

import android.app.DatePickerDialog
import android.graphics.Bitmap

data class HappyPlaceModel(
    val id : Int,
    val title : String,
    val image : Bitmap,
    val description : String,
    val date : String,
    val location : String,
    val latitude : Double,
    val longtitude: Double
)
