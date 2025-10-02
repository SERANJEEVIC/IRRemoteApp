package com.example.irremote.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY createdAt DESC")
    fun getAll(): List<DeviceEntity>

    @Insert
    fun insert(device: DeviceEntity): Long

    @Delete
    fun delete(device: DeviceEntity)
}
