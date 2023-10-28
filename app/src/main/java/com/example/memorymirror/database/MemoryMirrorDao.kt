package com.example.memorymirror.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryMirrorDao {
    @Insert
    suspend fun insert(memoryMirrorEntity: MemoryMirrorEntity)

    @Query("SELECT * FROM `memoryMirror-table`")
    fun fetchAllMemory(): Flow<List<MemoryMirrorEntity>>
}