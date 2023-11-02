package com.example.memorymirror.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "memoryMirror-table")
data class MemoryMirrorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val image: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),                 // Read 'id' as an Int
        parcel.readString() ?: "",        // Read 'title' as a String
        parcel.readString() ?: "",        // Read 'description' as a String
        parcel.readString() ?: "",        // Read 'date' as a String
        parcel.readString() ?: "",        // Read 'image' as a String
        parcel.readString() ?: "",        // Read 'location' as a String
        parcel.readDouble(),              // Read 'latitude' as a Double
        parcel.readDouble()               // Read 'longitude' as a Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)                // Write 'id' as an Int
        parcel.writeString(title)          // Write 'title' as a String
        parcel.writeString(description)    // Write 'description' as a String
        parcel.writeString(date)           // Write 'date' as a String
        parcel.writeString(image)          // Write 'image' as a String
        parcel.writeString(location)       // Write 'location' as a String
        parcel.writeDouble(latitude)       // Write 'latitude' as a Double
        parcel.writeDouble(longitude)      // Write 'longitude' as a Double
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemoryMirrorEntity> {
        override fun createFromParcel(parcel: Parcel): MemoryMirrorEntity {
            return MemoryMirrorEntity(parcel)
        }

        override fun newArray(size: Int): Array<MemoryMirrorEntity?> {
            return arrayOfNulls(size)
        }
    }
}
