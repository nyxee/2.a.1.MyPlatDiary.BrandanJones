package com.janus.a2a1myplatdiarybrandanjones.dto

import com.google.firebase.firestore.Exclude

data class Specimen(var plantName: String="", var latitude: String="", var longitude: String="", var description: String="", var datePlanted: String="", var specimenId : String="", var plantId: Int=0) {
    private var _plantEvents: ArrayList<PlantEvent> = ArrayList()
    var plantEvents: ArrayList<PlantEvent>
        get() = _plantEvents
        set(value) { _plantEvents = value}

    override fun toString(): String {
        return "$plantName $description $latitude $longitude"
    }
}