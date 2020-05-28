package com.janus.a2a1myplatdiarybrandanjones.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.janus.a2a1myplatdiarybrandanjones.RetrofitClientInstance
import com.janus.a2a1myplatdiarybrandanjones.dao.IPlantDAO
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantService {
    fun fetchPlants(plantName: String): MutableLiveData<ArrayList<Plant>>{

        System.out.println("????    fetchPlants 1")

        val TAG = PlantService::class.java.simpleName

        var _plants = MutableLiveData<ArrayList<Plant>>()
        val service = RetrofitClientInstance.retrofitInstance?.create(IPlantDAO::class.java)

        val call = service?.getAllPlants()
        System.out.println("????    fetchPlants 2")

        call?.enqueue(object: Callback<ArrayList<Plant>> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected exception
             * occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<ArrayList<Plant>>, t: Throwable) {
                System.out.println("onFailure:: ????  1")
                Log.e(TAG, "\t\tonFailure:: ${t.message}")
                val i = 1+1
                val j=1+1
            }

            /**
             * Invoked for a received HTTP response.
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<ArrayList<Plant>>, response: Response<ArrayList<Plant>>) {
                if (response.isSuccessful){
                    _plants.value = response.body()
                    Log.v(TAG, "\t\tNumber of Plants Returned:: ${_plants.value!!.size}")
//                    System.out.println("Numer of Plants Returned:: ${_plants.value!!.size}")

                }else{
                    Log.e(TAG, "\t\tReceived An Error Message From The Server.")
                }
            }
        })
        return _plants
    }
}