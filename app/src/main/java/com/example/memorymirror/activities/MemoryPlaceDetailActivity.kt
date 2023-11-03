package com.example.memorymirror.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorymirror.database.MemoryMirrorEntity
import com.example.memorymirror.databinding.ActivityMemoryPlaceDetailBinding

@Suppress("DEPRECATION")
class MemoryPlaceDetailActivity : AppCompatActivity() {
    private var binding: ActivityMemoryPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        var memoryList: MemoryMirrorEntity? = null
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            memoryList =
                intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }
        if (memoryList != null) {
            setSupportActionBar(binding?.toolbarAddMemoryPlacesDetail)
            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.title = memoryList.title
            }
            binding?.toolbarAddMemoryPlacesDetail?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            binding?.ivMemoryImage?.setImageURI(Uri.parse(memoryList.image))
            binding?.tvDescription?.text = memoryList.description
            binding?.tvLocation?.text = memoryList.location
        }
            binding?.btnMap?.setOnClickListener {
                val intent = Intent(this@MemoryPlaceDetailActivity,MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,memoryList)
                startActivity(intent)
            }
    }
}