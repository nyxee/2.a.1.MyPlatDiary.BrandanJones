package com.janus.a2a1myplatdiarybrandanjones.dto

import com.google.firebase.firestore.Exclude

data class Specimen(var plantName: String="", var latitude: String="", var longitude: String="", var description: String="", var datePlanted: String="", var specimenId : String="", var plantId: Int=0) {
    private var _plantEvents: ArrayList<PlantEvent> = ArrayList()
    var plantEvents: ArrayList<PlantEvent>
        @Exclude get() = _plantEvents //we wilsame the events ito A FireBase Collection, not as a member of the Speciment Collection.
        set(value) { _plantEvents = value}

    override fun toString(): String {
        return "$plantName $description $latitude $longitude"
    }
}