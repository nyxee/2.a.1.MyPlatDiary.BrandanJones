package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen

class DiaryMapsFragment : DiaryFragment() {
    private lateinit var mSpecimens: List<Specimen>
    private lateinit var mViewModel: MainViewModel
    val TAG = DiaryMapsFragment::class.java.simpleName
    private var mGoogleMap: GoogleMap? = null
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

        mGoogleMap = googleMap
        mMApReady = true
        updateMap()
    }

    //-----------------------------------------   CAMERA VIEW ---------------------------------------------------
    private var mMapBoundary: LatLngBounds? = null
    private lateinit var mUserLocation: LatLng

    private fun setCameraView(){
        val resolution = 0.05
        val bottomBoundary = mUserLocation.latitude-resolution
        val leftBoundary = mUserLocation.longitude-resolution
        val topBoundary = mUserLocation.latitude+resolution
        val rightBoundary = mUserLocation.longitude+resolution
        mMapBoundary = LatLngBounds(LatLng(bottomBoundary,leftBoundary), LatLng(topBoundary,rightBoundary))
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0))
//
        //===== OR:: ============== THIS HAS FAILED TO WORK FOR NOW.
//        val zoomLevel = 50f
//        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation, zoomLevel))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_diary_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(mOnMapReadyCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
        mViewModel.specimens.observe(viewLifecycleOwner, Observer {specimens->
            this.mSpecimens = specimens
            updateMap()
        })
    }

    private fun updateMap() {
        if (mMApReady && mSpecimens != null){
            mSpecimens.forEach {specimen->
                val location = LatLng(specimen.latitude.toDouble(), specimen.longitude.toDouble())
                Log.v(TAG, "\t\tspecimen.specimenId: ${specimen.specimenId}, specimen.plantName: ${specimen.plantName}")
                mGoogleMap!!.addMarker(MarkerOptions().position(location).title(specimen.specimenId).snippet(specimen.plantName))
                //mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                mUserLocation = location
            }
            setCameraView()
            setMapStyle()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    private fun setMapStyle() {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mGoogleMap!!.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        }catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
}