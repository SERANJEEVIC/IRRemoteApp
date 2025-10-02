package com.example.irremote.repository

import android.content.Context
import com.example.irremote.db.AppDatabase
import com.example.irremote.db.ButtonEntity
import com.example.irremote.db.DeviceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class DataRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val deviceDao = db.deviceDao()
    private val buttonDao = db.buttonDao()

    suspend fun getAllDevices(): List<DeviceEntity> = withContext(Dispatchers.IO) {
        val list = deviceDao.getAll()
        if (list.isEmpty()) {
            // seed with CeilingFan sample
            val id = deviceDao.insert(DeviceEntity(name = "CeilingFan"))
            val onPattern = listOf(9000,4500,560,560,560,1690) // sample (short)
            val offPattern = listOf(9000,2250,560,1690)
            buttonDao.insert(ButtonEntity(deviceId = id, label = "ON", freq = 38000, patternJson = Json.encodeToString(ListSerializer(Int.serializer()), onPattern)))
            buttonDao.insert(ButtonEntity(deviceId = id, label = "OFF", freq = 38000, patternJson = Json.encodeToString(ListSerializer(Int.serializer()), offPattern)))
        }
        return@withContext deviceDao.getAll()
    }

    suspend fun addDevice(name: String): Long = withContext(Dispatchers.IO) {
        deviceDao.insert(DeviceEntity(name = name))
    }

    suspend fun deleteDevice(device: DeviceEntity) = withContext(Dispatchers.IO) {
        buttonDao.deleteForDevice(device.id)
        deviceDao.delete(device)
    }

    suspend fun getButtons(deviceId: Long): List<ButtonEntity> = withContext(Dispatchers.IO) {
        buttonDao.getByDevice(deviceId)
    }

    suspend fun addButton(button: ButtonEntity): Long = withContext(Dispatchers.IO) {
        buttonDao.insert(button)
    }

    suspend fun updateButton(button: ButtonEntity) = withContext(Dispatchers.IO) {
        // Room update not implemented - simple delete/insert for brevity
        buttonDao.delete(button)
        buttonDao.insert(button)
    }

    // Export all data to JSON (saved in app external files dir)
    suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        val devices = deviceDao.getAll()
        val result = devices.map { d ->
            val buttons = buttonDao.getByDevice(d.id)
            mapOf(
                "device" to d.name,
                "createdAt" to d.createdAt,
                "buttons" to buttons.map { b ->
                    mapOf("label" to b.label, "freq" to b.freq, "patternJson" to b.patternJson)
                }
            )
        }
        val json = Json.encodeToString(ListSerializer(MapSerializer(String.serializer(), AnySerializer())), result)
        val file = context.getExternalFilesDir(null)?.resolve("ir_remote_export.json")
        file?.writeText(json)
        return@withContext file?.absolutePath ?: ""
    }

    // Import from JSON path (simple)
    suspend fun importJson(jsonText: String) = withContext(Dispatchers.IO) {
        // dummy implementation — user can paste JSON and repo can parse
    }
}
