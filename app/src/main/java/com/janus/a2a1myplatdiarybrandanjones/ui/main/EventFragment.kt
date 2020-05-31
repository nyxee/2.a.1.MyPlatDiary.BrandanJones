package com.janus.a2a1myplatdiarybrandanjones.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.PlantEvent
import kotlinx.android.synthetic.main.event_fragment.*

class EventFragment : DiaryFragment() {

    companion object {
        fun newInstance() = EventFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.event_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        activity?.let {
            viewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }
        // TODO: Use the ViewModel
        btnSaveEvent.setOnClickListener {
            savePlantEvent()
        }

        btnTakeEventPhoto.setOnClickListener {
            prepTakePhoto()
        }
    }

    private fun savePlantEvent() {

        with(PlantEvent()){
            type = actvEventType.text.toString()
            descriptiom = edtDesciption.text.toString()
            val qString = edtQuantity.text.toString()
            if (qString.isNotEmpty())
                quantity = qString.toDouble()
            units = actvUnits.text.toString()
            date = edtDate.text.toString()
            if (mPhotoURI != null)
                localPhotoURI = mPhotoURI.toString()
            viewModel.specimen.plantEvents.add(this)
            clearAll()
        }
    }

    private fun clearAll() {
        actvEventType.setText("")
        edtDesciption.setText("")
        edtQuantity.setText("")
        actvUnits.setText("")
        edtDate.setText("")
        mPhotoURI = null
    }

}