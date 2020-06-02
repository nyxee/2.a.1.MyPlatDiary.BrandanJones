package com.janus.a2a1myplatdiarybrandanjones.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.janus.a2a1myplatdiarybrandanjones.RetrofitClientInstance
import com.janus.a2a1myplatdiarybrandanjones.dao.IPlantDAO
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantService {
    //make the functio eligible to be called as a coroutine by adding the suspend modifier.
    internal suspend fun fetchPlants(plantName: String){
        withContext(Dispatchers.IO) {
            println("????    fetchPlants 1")
            val TAG = PlantService::class.java.simpleName

            val service = RetrofitClientInstance.retrofitInstance?.create(IPlantDAO::class.java)

            val plants:Deferred<ArrayList<Plant>?> = async{ service?.getAllPlants() }
            updateLocalPlants(plants.await())
        }
    }

    private suspend fun updateLocalPlants(plants: ArrayList<Plant>?) {
        var sizeOfPlants = plants?.size
    }
}