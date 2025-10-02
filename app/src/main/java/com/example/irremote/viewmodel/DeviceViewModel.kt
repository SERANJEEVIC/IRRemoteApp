package com.example.irremote.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.irremote.db.ButtonEntity
import com.example.irremote.repository.DataRepository
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = DataRepository(application.applicationContext)
    val buttons = MutableLiveData<List<ButtonEntity>>(emptyList())

    fun load(deviceId: Long) {
        viewModelScope.launch {
            buttons.postValue(repo.getButtons(deviceId))
        }
    }

    fun addButton(deviceId: Long, label: String, freq: Int, patternJson: String) {
        viewModelScope.launch {
            repo.addButton(ButtonEntity(deviceId = deviceId, label = label, freq = freq, patternJson = patternJson))
            buttons.postValue(repo.getButtons(deviceId))
        }
    }

    fun updateButton(button: ButtonEntity) {
        viewModelScope.launch {
            repo.updateButton(button)
            buttons.postValue(repo.getButtons(button.deviceId))
        }
    }
}
