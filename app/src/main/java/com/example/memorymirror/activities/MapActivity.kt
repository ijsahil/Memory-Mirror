package com.example.memorymirror.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.memorymirror.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private var binding: ActivityMapBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Map"
            binding?.toolbarMap?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}