package com.example.memorymirror.activities

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.memorymirror.R
import com.example.memorymirror.database.MemoryMirrorDao
import com.example.memorymirror.database.MemoryMirrorEntity
import com.example.memorymirror.databinding.ActivityAddMemoryPlaceBinding
import com.example.memorymirror.models.MemoryMirrorApp
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


@Suppress("DEPRECATION")
class AddMemoryPlaceActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityAddMemoryPlaceBinding
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var memoryMirrorDao: MemoryMirrorDao
    private var saveImageToInternalStorage: Uri? = null
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
                onBackPressedDispatcher.onBackPressed()
            }


        dateSetListener = OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        binding.btnSave.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
        memoryMirrorDao = (application as MemoryMirrorApp).db.memoryMirrorDao()
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

            R.id.btnSave -> {
                addDataToDatabase(memoryMirrorDao)
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

    @SuppressLint("QueryPermissionsNeeded")
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        val contentResolver = contentResolver
                        try {
                            val inputStream = contentResolver.openInputStream(selectedImageUri)
                            val photo = BitmapFactory.decodeStream(inputStream)
                            saveImageToInternalStorage = saveImageToInternalStorage(photo)
                            Log.e("Saved image :", "Path :: $saveImageToInternalStorage")
                            // Now you have the 'bitmap' with the selected image.
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // Handle the exception or show an error message.
                        }
                        binding.ivDisplayImage.setImageURI(selectedImageUri)
                    }
                }

                CAMERA_REQUEST_CODE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    saveImageToInternalStorage = saveImageToInternalStorage(photo)
                    Log.e("Saved image :", "Path :: $saveImageToInternalStorage")
                    binding.ivDisplayImage.setImageBitmap(photo)
                }
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("IMAGE_DIRECTORY", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun addDataToDatabase(memoryMirrorDao: MemoryMirrorDao) {
        when {
            binding.etTitle.text.isNullOrBlank() -> {
                Toast.makeText(
                    this@AddMemoryPlaceActivity,
                    "Title cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.etDescription.text.isNullOrBlank() -> {
                Toast.makeText(
                    this@AddMemoryPlaceActivity,
                    "Description cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.etDate.text.isNullOrBlank() -> {
                Toast.makeText(
                    this@AddMemoryPlaceActivity,
                    "Date cannot be blank",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.etLocation.text.isNullOrBlank() -> {
                Toast.makeText(
                    this@AddMemoryPlaceActivity,
                    "Location cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }

            saveImageToInternalStorage == null -> {
                Toast.makeText(
                    this@AddMemoryPlaceActivity,
                    "Please took the image",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                val title = binding.etTitle.text.toString()
                val description = binding.etDescription.text.toString()
                val date = binding.etDate.text.toString()
                val location = binding.etLocation.text.toString()
                val image = saveImageToInternalStorage.toString()
                val memoryMirrorEntity = MemoryMirrorEntity(
                    title = title,
                    description = description,
                    date = date,
                    image = image,
                    location = location
                )
                lifecycleScope.launch {
                    memoryMirrorDao.insert(memoryMirrorEntity)
                    Toast.makeText(
                        this@AddMemoryPlaceActivity,
                        "Memory made successfully !!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
        }

    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }
}