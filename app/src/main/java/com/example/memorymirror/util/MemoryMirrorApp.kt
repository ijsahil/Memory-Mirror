package com.example.memorymirror.util

import android.app.Application
import com.example.memorymirror.database.MemoryMirrorDatabase

class MemoryMirrorApp : Application() {
    val db by lazy {
        MemoryMirrorDatabase.getInstance(this)
    }
}