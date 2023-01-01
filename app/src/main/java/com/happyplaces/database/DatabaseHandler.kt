package com.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.happyplaces.models.HappyPlaceModel
import java.io.ByteArrayOutputStream
import java.sql.SQLException

class DatabaseHandler(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable" //table name

        //all collumns name
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGTITUDE = "longtitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $TABLE_HAPPY_PLACE (" +
                    "$KEY_ID INTEGER PRIMARY KEY," +
                    "$KEY_TITLE TEXT," +
                    "$KEY_IMAGE TEXT," +
                    "$KEY_DESCRIPTION TEXT," +
                    "$KEY_DATE TEXT," +
                    "$KEY_LOCATION TEXT," +
                    "$KEY_LATITUDE REAL," +
                    "$KEY_LONGTITUDE REAL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(KEY_TITLE, happyPlace.title)
            put(KEY_IMAGE, happyPlace.image)
            put(KEY_DESCRIPTION, happyPlace.description)
            put(KEY_DATE, happyPlace.date)
            put(KEY_LOCATION, happyPlace.location)
            put(KEY_LATITUDE, happyPlace.latitude)
            put(KEY_LONGTITUDE, happyPlace.longtitude)
        }
        Log.d("DEBUG", "Inserting values: $values")
        val success = db.update(TABLE_HAPPY_PLACE,values , KEY_ID + "=" + happyPlace.id,null )
        return success
    }


    fun insertHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = writableDatabase


        val values = ContentValues().apply {
            put(KEY_TITLE, happyPlace.title)
            put(KEY_IMAGE, happyPlace.image)
            put(KEY_DESCRIPTION, happyPlace.description)
            put(KEY_DATE, happyPlace.date)
            put(KEY_LOCATION, happyPlace.location)
            put(KEY_LATITUDE, happyPlace.latitude)
            put(KEY_LONGTITUDE, happyPlace.longtitude)
        }
        Log.d("DEBUG", "Inserting values: $values")
        val result = db.insert(TABLE_HAPPY_PLACE, null, values)
        return result
    }

    fun getHappyPlacesList() : ArrayList<HappyPlaceModel> {
        val happyPlacesList = ArrayList<HappyPlaceModel>()
        val db = readableDatabase
        try {
            val cursor = db.rawQuery("SELECT * FROM $TABLE_HAPPY_PLACE", null)

            while (cursor.moveToNext()) {
                //convert byteArray to Bitmap

                val model = HappyPlaceModel(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGTITUDE))
                )
                happyPlacesList.add(model)
            }
            cursor.close()
        } catch (e : SQLException) {
            db.execSQL("SELECT * FROM $TABLE_HAPPY_PLACE", null)
            return ArrayList()
        }
        return happyPlacesList
    }

}