package com.janus.a2a1myplatdiarybrandanjones.dto

data class PlantEvent(var type:  String="", var  date:  String="", var  quantity: Double?=0.0, var  units:  String="", var  descriptiom:  String="", var localPhotoURI: String?=null, var id: String="" ) {

    override fun toString(): String {
        return "$type\n$quantity\n$units\n$descriptiom"
    }
}