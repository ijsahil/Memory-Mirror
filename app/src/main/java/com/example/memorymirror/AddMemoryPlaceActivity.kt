package com.example.memorymirror

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.memorymirror.databinding.ActivityAddMemoryPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddMemoryPlaceActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityAddMemoryPlaceBinding
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemoryPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarAddMemoryPlaces)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbarAddMemoryPlaces
            .setNavigationOnClickListener {
                onBackPressed()
            }
        dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.etDate -> {
                DatePickerDialog(
                    this@AddMemoryPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.tvAddImage -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Select Image Source")
                alertDialog.setItems(
                    arrayOf("Choose Image from Gallery", "Use Camera")
                ) { _, which ->
                    when (which) {
                        0 -> {
                            chooseImageFromGalleryOrCamera(
                                arrayListOf(
                                    READ_EXTERNAL_STORAGE,
                                    WRITE_EXTERNAL_STORAGE
                                )
                            ) { openGallery() }
                            Log.e("Checked", "Yes the flow is reached here")
                        }

                        1 -> {
                            chooseImageFromGalleryOrCamera(arrayListOf(CAMERA)) { openCamera() }
                        }
                    }
                }
                val dialog = alertDialog.create()
                dialog.show()
            }
        }
    }

    private fun chooseImageFromGalleryOrCamera(permissionList: List<String>, function: () -> Unit) {
        Dexter.withContext(this@AddMemoryPlaceActivity)
            .withPermissions(
                permissionList
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        function.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    permissionToken: PermissionToken?
                ) {
                    showSettingsDialog()
                }
            })
            .onSameThread().check()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityIfNeeded(galleryIntent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityIfNeeded(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(
                this@AddMemoryPlaceActivity,
                "Camera not available on this device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@AddMemoryPlaceActivity)
        builder.setTitle("Need Permissions")

        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // Change this format as needed
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    binding.ivDisplayImage.setImageURI(selectedImageUri)
                }

                CAMERA_REQUEST_CODE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    binding.ivDisplayImage.setImageBitmap(photo)
                }
            }
        }
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }
}