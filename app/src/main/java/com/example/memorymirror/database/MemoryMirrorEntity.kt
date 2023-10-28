package com.example.memorymirror.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "memoryMirror-table")
data class MemoryMirrorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 ,
    val title : String ,
    val description : String ,
    val date : String ,
    val image : String ,
    val location : String ,
    val latitude : Double = 0.0 ,
    val longitude : Double = 0.0
)
