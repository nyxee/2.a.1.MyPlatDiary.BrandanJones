package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.janus.a2a1myplatdiarybrandanjones.dto.Photo
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.dto.PlantEvent
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService

class MainViewModel : ViewModel() {
    private var storageReferenence: StorageReference
    var plants = MutableLiveData<ArrayList<Plant>>()
    var _plantService = PlantService()
    private var firestore: FirebaseFirestore
    val TAG = MainViewModel::class.java.simpleName
    var _specimens = MutableLiveData<ArrayList<Specimen>>()
    var _specimen = Specimen()
    private var _events = MutableLiveData<List<PlantEvent>>()

    internal var specimens:MutableLiveData<ArrayList<Specimen>>
        get() { return _specimens}
        set(value) {_specimens = value}

    internal var specimen: Specimen
        get() {return _specimen}
        set(value) {_specimen = value}

    internal var plantService : PlantService
        get() { return _plantService }
        set(value) {_plantService = value}

    internal var events : MutableLiveData<List<PlantEvent>>
        get() { return _events}
        set(value) {_events = value}

    fun fetchPlants(plantName: String) {
        plants = _plantService.fetchPlants(plantName)
        System.out.println("MainViewModel::fetchPlants($plantName) :: Returned: ${plants.value}")

    }
    internal fun fetchEvents(){
        firestore.collection("Specimens").document(specimen.specimenId)
            .collection("Events").also {eventsCollection->
                eventsCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    querySnapshot?.toObjects(PlantEvent::class.java).also {
                        _events.postValue(it!!)
                    }
                }
            }
    }
    internal fun save(event: PlantEvent) {
        firestore.collection("Specimens")
            .document(specimen.specimenId)
            .collection("Event").document().also {eventDocRef->
                event.id = eventDocRef.id    //save the ID in our event object then store it in FB.
                eventDocRef.set(event)
            }

    }

    fun save(specimen: Specimen, photos: ArrayList<Photo>, user: FirebaseUser) {
        val document = firestore.collection("Specimens").document()
        specimen.specimenId = document.id
        document.set(specimen)
            .addOnSuccessListener {
                Log.v(TAG, "\t\t\tspecimen saved with id: ${specimen.specimenId}")
                if (photos != null && photos.size > 0)
                    savePhotos(specimen, photos, user)
            }
            .addOnFailureListener {
                Log.e(TAG, "\t\tFAILED::::  ${it.localizedMessage}")
            }.apply {

            }
    }

    private fun savePhotos(specimen: Specimen, photos: ArrayList<Photo>, user: FirebaseUser) {
        firestore.collection("Specimens")
            .document(specimen.specimenId) //to update an existing specimen
            .collection("Photos")
            .also { collection ->
                photos.forEach{ photo->

                    val photoDocumentRef =  collection.document() //CREATE THE PHOTO DOCUMENT REFERENCE
                    photo.id = photoDocumentRef.id

                    var uri = Uri.parse(photo.localUri)
                    val imageStorageRef = storageReferenence.child("images/" + user.uid + "/" + uri.lastPathSegment)
                    Log.v(TAG, "\t\timageStorageRef:: $imageStorageRef")
                    val uploadTask = imageStorageRef.putFile(uri)
                    uploadTask.addOnSuccessListener {
                        val downloadUrl = imageStorageRef.downloadUrl
                        downloadUrl.addOnSuccessListener {

                            photo.remoteUri = it.toString()
                            // update our Cloud Firestore with the public image URI.
                            updatePhotoDatabase(photo, photoDocumentRef)
                        }
                    }
                    uploadTask.addOnFailureListener {
                        Log.e(TAG, "Uploading Image ID:: [${photo.id }] to SpecimenID: [${specimen.specimenId}] FAILED!!")
                    }
                    uploadTask.addOnCanceledListener {
                        Log.e(TAG, "Uploading Image ID:: [${photo.id }] to SpecimenID: [${specimen.specimenId}] WAS CANCELLED BY SOMETHING!!")
                    }
                }
            }
    }
    private fun updatePhotoDatabase(photo: Photo, photoDocumentRef: DocumentReference) {
        photoDocumentRef.set(photo)
            .addOnSuccessListener {
                Log.v(TAG, "\t\t\tphoto saved")
            }
            .addOnFailureListener {
                Log.e(TAG, "\t\tFAILED::::  ${it.localizedMessage}")
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

        storageReferenence = FirebaseStorage.getInstance().reference

        listenToSpecimens()
    }
}
