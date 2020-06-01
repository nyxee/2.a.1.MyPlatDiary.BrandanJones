package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.janus.a2a1myplatdiarybrandanjones.MainActivity
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
        btnBackToSpecimen.setOnClickListener {
            (activity as MainActivity).onSwipeRight()
        }
        rvEvents.hasFixedSize()
        rvEvents.layoutManager = LinearLayoutManager(context)
        rvEvents.itemAnimator = DefaultItemAnimator()
        rvEvents.adapter = EventAdapter(viewModel.specimen.plantEvents, R.layout.rowlayout)

    }

    private fun savePlantEvent() {

        with(PlantEvent()){
            type = actvEventType.text.toString()
            descriptiom = edtDescription.text.toString()
            val qString = edtQuantity.text.toString()
            if (qString.isNotEmpty())
                quantity = qString.toDouble()
            units = actvUnits.text.toString()
            date = edtDate.text.toString()
            if (mPhotoURI != null)
                localPhotoURI = mPhotoURI.toString()
            viewModel.specimen.plantEvents.add(this)
            viewModel.save(this)
            clearAll()
            rvEvents.adapter!!.notifyDataSetChanged()
        }
    }

    private fun clearAll() {
        actvEventType.setText("")
        edtDescription.setText("")
        edtQuantity.setText("")
        actvUnits.setText("")
        edtDate.setText("")
        mPhotoURI = null
    }

}