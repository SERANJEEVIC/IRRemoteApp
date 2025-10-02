package com.example.irremote.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface ButtonDao {
    @Query("SELECT * FROM buttons WHERE deviceId = :deviceId ORDER BY createdAt DESC")
    fun getByDevice(deviceId: Long): List<ButtonEntity>

    @Insert
    fun insert(button: ButtonEntity): Long

    @Delete
    fun delete(button: ButtonEntity)

    @Query("DELETE FROM buttons WHERE deviceId = :deviceId")
    fun deleteForDevice(deviceId: Long)
}
