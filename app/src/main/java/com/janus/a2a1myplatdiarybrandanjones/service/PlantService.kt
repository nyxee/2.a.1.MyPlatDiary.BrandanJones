package com.janus.a2a1myplatdiarybrandanjones.service

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.janus.a2a1myplatdiarybrandanjones.RetrofitClientInstance
import com.janus.a2a1myplatdiarybrandanjones.dao.ILocalPlantDAO
import com.janus.a2a1myplatdiarybrandanjones.dao.IPlantDAO
import com.janus.a2a1myplatdiarybrandanjones.dao.PlantDatabase
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import kotlinx.coroutines.*

class PlantService(var application: Application) {
    //make the functio eligible to be called as a coroutine by adding the suspend modifier.
    internal suspend fun fetchPlants(plantName: String){
        withContext(Dispatchers.IO) {
            println("????    fetchPlants 1")
            val TAG = PlantService::class.java.simpleName
            Log.v(TAG, "\t\t fetchPlants($plantName)....")
            val service = RetrofitClientInstance.retrofitInstance?.create(IPlantDAO::class.java)

            val plants:Deferred<ArrayList<Plant>?> = async{ service?.getAllPlants() }
            updateLocalPlants(plants.await())
        }
    }

    private suspend fun updateLocalPlants(plants: ArrayList<Plant>?) {
        getLocalPlantDAO()
            .insertAll(plants!!)
    }

    internal fun getLocalPlantDAO(): ILocalPlantDAO {
        Room.databaseBuilder(application, PlantDatabase::class.java, "Diary")
            .build().also {db->
                return db.localPlantDAO()
            }
    }
}