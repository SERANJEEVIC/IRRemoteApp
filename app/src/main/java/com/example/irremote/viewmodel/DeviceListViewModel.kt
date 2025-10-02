package com.example.irremote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.irremote.db.DeviceEntity
import com.example.irremote.repository.DataRepository
import kotlinx.coroutines.launch

class DeviceListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = DataRepository(application.applicationContext)
    val devices = MutableLiveData<List<DeviceEntity>>(emptyList())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            devices.postValue(repo.getAllDevices())
        }
    }

    fun addDevice(name: String) {
        viewModelScope.launch {
            repo.addDevice(name)
            devices.postValue(repo.getAllDevices())
        }
    }

    fun deleteDevice(device: DeviceEntity) {
        viewModelScope.launch {
            repo.deleteDevice(device)
            devices.postValue(repo.getAllDevices())
        }
    }

    fun exportToJson(context: android.content.Context, cb: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val path = repo.exportJson()
                cb(true, path)
            } catch (e: Exception) {
                cb(false, e.message ?: "Export failed")
            }
        }
    }

    fun importFromJson(context: android.content.Context, cb: (Boolean, String) -> Unit) {
        // For brevity: open PICK document or ask user to paste JSON. We provide placeholder
        cb(false, "Use Import from file or paste JSON (not implemented in this demo)")
    }
}
