package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.janus.a2a1myplatdiarybrandanjones.dto.LocationDetails

//TODO, should context be Application or Context???
class LocationliveData(var context: Context): LiveData<LocationDetails>() {

    private val TAG = LocationliveData::class.java.simpleName

    private var mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")  //PROVIDED FOR IN MAINfRAGMENT.prepRequestLocationUpdates()
    override fun onActive() { //Someone is observing the LiveData
        super.onActive()
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {location ->
            location?.also {
                setLocationData(it)
            }
        }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission") //PROVIDED FOR IN MAINfRAGMENT.prepRequestLocationUpdates()
    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null)
    }

    private val locationCallBack = object: LocationCallback(){
        override fun onLocationResult(locathionResult: LocationResult?) {
            super.onLocationResult(locathionResult)

            locathionResult ?: return

            locathionResult.locations.forEach{
                setLocationData(it)
            }
        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            super.onLocationAvailability(locationAvailability)
            Log.v(TAG, "\t\tLocation Availability: $locationAvailability")
        }
    }

    private fun setLocationData(location: Location) {
        value = LocationDetails("${location.latitude}", "${location.longitude}")
    }

    override fun onInactive() {
        super.onInactive()
        mFusedLocationProviderClient.removeLocationUpdates(locationCallBack)
    }

    companion object {
        val ONE_MINUTE = 60000L
        val locationRequest = LocationRequest.create().apply {
            interval = ONE_MINUTE
            fastestInterval =  ONE_MINUTE/4
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }
}
