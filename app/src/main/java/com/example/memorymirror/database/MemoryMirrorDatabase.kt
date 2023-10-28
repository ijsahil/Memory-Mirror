package com.example.memorymirror.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [MemoryMirrorEntity::class] , version = 1)
abstract class MemoryMirrorDatabase : RoomDatabase() {

    abstract fun memoryMirrorDao() : MemoryMirrorDao
    companion object {

        @Volatile
        private var INSTANCE: MemoryMirrorDatabase? = null


        fun getInstance(context: Context): MemoryMirrorDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MemoryMirrorDatabase::class.java,
                        "memory_mirror_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}