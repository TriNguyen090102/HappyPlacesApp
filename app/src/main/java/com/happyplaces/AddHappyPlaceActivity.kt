package com.happyplaces

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.happyplaces.databinding.ActivityAddHappyPlaceBinding
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

private const val SELECT_IMAGE_REQUEST_CODE = 1
private const val REQUEST_IMAGE_CAPTURE = 2

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

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
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
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
        }
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
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
            }).check()
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
                    // Take a photo with the camera
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
                1 -> {
                    // Select an image from the device's storage
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
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
                    // Use the photo (e.g. display it in an ImageView)
                    binding?.ivPlaceImage?.setImageBitmap(bitmap)
                }
                SELECT_IMAGE_REQUEST_CODE -> {
                    // Get the selected image's URI
                    val selectedImageUri = data?.data
                    // Use the selected image (e.g. display it in an ImageView)
                    binding?.ivPlaceImage?.setImageURI(selectedImageUri)
                }
            }
        }
    }
}