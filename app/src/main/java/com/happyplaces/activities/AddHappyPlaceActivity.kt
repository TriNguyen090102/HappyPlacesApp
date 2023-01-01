package com.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.happyplaces.database.DatabaseHandler
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

private const val RESULT_OK = 0
private const val SELECT_IMAGE_REQUEST_CODE = 1
private const val REQUEST_IMAGE_CAPTURE = 2

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null
    private var uriImage: Uri? = null
    private var mHappyPlaceDetailsChange : HappyPlaceModel?= null
    private var mLatitude: Double = 0.0 // A variable which will hold the latitude value.
    private var mLongitude: Double = 0.0 // A variable which will hold the longitude value.

    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        // This is used to align the xml view to this class
        setContentView(binding?.root)
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetailsChange =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }
        // START

        setSupportActionBar(toolbar_add_place) // Use the toolbar to set the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // This is to use the home back button.
        // Setting the click event to the back button
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
        if(mHappyPlaceDetailsChange != null)
        {
            supportActionBar?.title = "Edit Happy Place"

            et_title.setText(mHappyPlaceDetailsChange!!.title)
            et_description.setText(mHappyPlaceDetailsChange!!.description)
            et_date.setText(mHappyPlaceDetailsChange!!.date)
            et_location.setText(mHappyPlaceDetailsChange!!.location)
            mLatitude = mHappyPlaceDetailsChange!!.latitude
            mLongitude = mHappyPlaceDetailsChange!!.longtitude


            iv_place_image.setImageURI(Uri.parse(mHappyPlaceDetailsChange!!.image))

            btn_save.text = "UPDATE"
        }
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
    }

    private fun setupDatePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Update the EditText with the selected date
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            binding?.etDate?.setText(dateFormat.format(selectedDate.time))
        }

        DatePickerDialog(
            this@AddHappyPlaceActivity, dpd,
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onClick(it: View?) {
        when (it) {
            binding?.tvAddImage -> {
                requestPermissions()
            }

            binding?.etDate -> {
                setupDatePickerDialog()
            }

            binding?.btnSave -> {
                if(mHappyPlaceDetailsChange != null)
                {
                    updateInDatabase()
                    setResult(RESULT_OK)
                    finish()
                }

                else
                {
                    when {
                        binding?.etTitle?.text.isNullOrEmpty() -> {
                            Toast.makeText(this, "Title can not be empty", Toast.LENGTH_LONG).show()
                        }

                        binding?.etDescription?.text.isNullOrEmpty() -> {
                            Toast.makeText(this, "Description can not be empty", Toast.LENGTH_LONG)
                                .show()
                        }

                        binding?.etDate?.text.isNullOrEmpty() -> {
                            Toast.makeText(this, "Date can not be empty", Toast.LENGTH_LONG).show()
                        }

                        binding?.etLocation?.text.isNullOrEmpty() -> {
                            Toast.makeText(this, "Location can not be empty", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            addToTheDatabase()
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun updateInDatabase() {
        val databaseHandler = DatabaseHandler(this)
        var modelUri: Uri? = null
        if (uriImage == null) {
            //get the uri of default image
            modelUri = getDefaultImageUri()
        } else {
            modelUri = uriImage
        }

        val model = HappyPlaceModel(
            mHappyPlaceDetailsChange!!.id,
            binding?.etTitle?.text.toString(),
            modelUri.toString(),
            binding?.etDescription?.text.toString(),
            binding?.etDate?.text.toString(),
            binding?.etLocation?.text.toString(),
            0.0,
            0.0
        )
        try {
            val success = databaseHandler.updateHappyPlace(model)
            if (success > 0) {
                Toast.makeText(this, "Update successful", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Something wrong! Please update again", Toast.LENGTH_LONG).show()
            }
            uriImage = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addToTheDatabase() {
        val databaseHandler = DatabaseHandler(this)
        var modelUri: Uri? = null
        if (uriImage == null) {
            //get the uri of default image
            modelUri = getDefaultImageUri()
        } else {
            modelUri = uriImage
        }

        val model = HappyPlaceModel(
            0,
            binding?.etTitle?.text.toString(),
            modelUri.toString(),
            binding?.etDescription?.text.toString(),
            binding?.etDate?.text.toString(),
            binding?.etLocation?.text.toString(),
            0.0,
            0.0
        )


        try {
            val result = databaseHandler.insertHappyPlace(model)

            if (result > 0) {
                Toast.makeText(this, "Save successful", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Something wrong! Please save again", Toast.LENGTH_LONG).show()
            }
            uriImage = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getDefaultImageUri(): Uri {
        val imageResource =
            resources.getIdentifier("add_screen_image_placeholder", "drawable", packageName)
        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(
                imageResource
            ) + '/' + resources.getResourceTypeName(imageResource) + '/' + resources.getResourceEntryName(
                imageResource
            )
        )
        return imageUri
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // Check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // All permissions are granted, display the image picker or camera capture UI
                        showChoseDialog()
                    }

                    // Check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showExplainingDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>, token: PermissionToken
                ) {
                    // Show a dialog explaining the reasons for requesting the permissions
                    token.continuePermissionRequest()
                }
            }).onSameThread().check()
    }

    private fun showExplainingDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions Required")
        builder.setMessage("This app requires storage and camera permissions to function properly. Please go to settings to enable these permissions.")

        builder.setPositiveButton("Go to Settings") { _, _ ->
            // Direct the user to the app's settings page
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            // Dismiss the dialog
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showChoseDialog() {
        val builder = AlertDialog.Builder(this@AddHappyPlaceActivity)
        builder.setTitle("Choose image source")
        builder.setItems(arrayOf("Camera", "Gallery")) { dialog, which ->
            when (which) {
                0 -> {
                    try {
                        // Take a photo with the camera
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                1 -> {
                    try {
                        // Select an image from the device's storage
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Get the photo taken with the camera
                    val bitmap = data?.extras?.get("data") as Bitmap
                    //save the image to gallery and get the uri
                    val uri = saveImageToGallery(bitmap)
                    // Use the photo (e.g. display it in an ImageView)
                    binding?.ivPlaceImage?.setImageBitmap(bitmap)
                    uriImage = uri
                }
                SELECT_IMAGE_REQUEST_CODE -> {
                    // Get the selected image's URI
                    val selectedImageUri = data?.data
                    val contentResolver = contentResolver
                    val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val uri = saveImageToGallery(bitmap)
                    // Use the selected image (e.g. display it in an ImageView)
                    binding?.ivPlaceImage?.setImageBitmap(bitmap)
                    uriImage = uri
                }
            }
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Create a ContentValues object and set the desired values for the image
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.TITLE, "")
                    put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                }

                // Get a ContentResolver and insert the image into the MediaStore
                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                // Open an OutputStream for the URI and write the Bitmap data to it
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                }
                return uri!!
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, "${System.currentTimeMillis()}.jpg")
                val imageuri = image.toURI()
                val uri = Uri.parse(imageuri.toString())
                return uri
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Uri.EMPTY
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        uriImage = null
    }


}