package com.janus.a2a1myplatdiarybrandanjones.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService

class MainViewModel : ViewModel() {
    var plants = MutableLiveData<ArrayList<Plant>>()
    var plantService = PlantService()

    fun fetchPlants(plantName: String) {
        //System.out.println("MainViewModel::fetchPlants($plantName) ")
        plants = plantService.fetchPlants(plantName)
    }



    // TODO: Implement the ViewModel
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}
