package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen

class DiaryMapsFragment : DiaryFragment() {
    val TAG = DiaryMapsFragment::class.java.simpleName
    private var mMap: GoogleMap? = null
    private var mMApReady = false

    val firestore = FirebaseFirestore.getInstance()

    private val mOnMapReadyCallback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

        mMap = googleMap
        mMApReady = true
        listenToSpecimens()
        updateMap()
    }

    private fun listenToSpecimens() {
        firestore.collection("Specimens").addSnapshotListener { snapshot, e ->
            // if there is an exception we want to skip.
            if (e != null) {
                Log.w(TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            // if we are here, we did not encounter an exception
            snapshot?.documents?.forEach {
                it.toObject(Specimen::class.java)?.let {specimen->
                    val location = LatLng(specimen.latitude.toDouble(), specimen.longitude.toDouble())
                    mMap!!.addMarker(MarkerOptions().position(location).title(specimen.specimenId).snippet(specimen.plantName))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                }
            }
        }
    }
    private fun updateMap() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_diary_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(mOnMapReadyCallback)
    }
}