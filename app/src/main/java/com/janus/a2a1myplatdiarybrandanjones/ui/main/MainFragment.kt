package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.janus.a2a1myplatdiarybrandanjones.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    companion object {
        val TAG = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.plants.observe(viewLifecycleOwner, Observer {
            Log.v(TAG, "\t\t Number of Plants Returned:: ${it.size}")
            actPlantName.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, it))
        })
    }

}
