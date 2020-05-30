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
    var _specimens = MutableLiveData<ArrayList<Specimen>>()

    internal var specimens:MutableLiveData<ArrayList<Specimen>>
        get() { return _specimens}
        set(value) {_specimens = value}

    fun fetchPlants(plantName: String) {
        System.out.println("MainViewModel::fetchPlantsts($plantName) ")
        plants = plantService.fetchPlants(plantName)
        System.out.println("MainViewModel::fetchPlantsts($plantName) :: Sixe REturned: ${plants.value}")

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

    /**
     * This will hear any updates from Firestore
     */
    private fun listenToSpecimens() {
        firestore.collection("Specimens").addSnapshotListener { snapshot, e ->
            // if there is an exception we want to skip.
            if (e != null) {
                Log.w(TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            if (snapshot != null) {
                // now, we have a populated shapshot
                val allSpecimens = ArrayList<Specimen>()
                snapshot.documents.forEach {
                    it.toObject(Specimen::class.java)?.let {
                        allSpecimens.add(it)
                    }
                }
                _specimens.value = allSpecimens
            }
        }
    }

    init {
        fetchPlants("e")
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        listenToSpecimens()
    }
}
