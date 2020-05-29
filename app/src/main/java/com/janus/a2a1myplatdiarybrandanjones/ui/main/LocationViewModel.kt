package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application){
    private val locationLiveData = LocationliveData(application)
    fun getLocationLiveData() = locationLiveData

}