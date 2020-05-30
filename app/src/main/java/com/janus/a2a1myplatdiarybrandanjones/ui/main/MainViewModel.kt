package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService

class MainViewModel : ViewModel() {
    var plants = MutableLiveData<ArrayList<Plant>>()
    var plantService = PlantService()
    private lateinit var firestore: FirebaseFirestore
val TAG = MainViewModel::class.java.simpleName

    fun fetchPlants(plantName: String) {
        System.out.println("MainViewModel::fetchPlantsts($plantName) ")
        plants = plantService.fetchPlants(plantName)
        System.out.println("MainViewModel::fetchPlantsts($plantName) :: Sixe REturned: ${plants.value}")
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    fun save(specimen: Specimen) {
        val document = firestore.collection("Specimens").document()
        specimen.specimenId = document.id
        document.set(specimen)
            .addOnSuccessListener {
                Log.v(TAG, "\t\t\tdocument saved")
            }
            .addOnFailureListener {
                Log.e(TAG, "\t\t\tFAILED::::  ${it.localizedMessage}")
            }.apply {

            }
    }


    // TODO: Implement the ViewModel
    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    init {
        fetchPlants("e")
    }
}
