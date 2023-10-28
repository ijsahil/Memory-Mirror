package com.example.memorymirror

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var fabAddBtn : FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAddBtn = findViewById(R.id.fabAddBtn)
        fabAddBtn?.setOnClickListener{
            val intent = Intent(this,AddMemoryPlaceActivity::class.java)
            startActivity(intent)
        }
    }
}