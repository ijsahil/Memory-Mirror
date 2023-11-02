package com.example.memorymirror.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryMirrorDao {
    @Insert
    suspend fun insert(memoryMirrorEntity: MemoryMirrorEntity)

    @Delete
    suspend fun delete(memoryMirrorEntity: MemoryMirrorEntity)

    @Update
    suspend fun update(memoryMirrorEntity: MemoryMirrorEntity)

    @Query("SELECT * FROM `memoryMirror-table`")
    fun fetchAllMemory(): Flow<List<MemoryMirrorEntity>>
}