package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService
import kotlinx.coroutines.launch

class ApplicationViewModel(application: Application) : AndroidViewModel(application){
    private val locationLiveData = LocationliveData(application)
    fun getLocationLiveData() = locationLiveData
    private var _plantService = PlantService(application)


    fun fetchPlants(plantName: String) {
        viewModelScope.launch {
            _plantService.fetchPlants(plantName)
        }
    }

    internal  var plantService: PlantService
        get() = _plantService
        set(value) {_plantService = value}

    init {
        fetchPlants("e")
    }
}