package com.janus.a2a1myplatdiarybrandanjones.service

import androidx.lifecycle.MutableLiveData
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant

class PlantService {
    fun fetchPlants(plantName: String): MutableLiveData<ArrayList<Plant>>{
        System.out.println("PlantService::fetchPlants($plantName)")

        return MutableLiveData<ArrayList<Plant>>()
    }
}