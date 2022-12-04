package com.sidukov.weatherapp.ui.fragment_location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.LocationRepository

class LocationFragment: Fragment() {

    private val adapterLocation = LocationViewAdapter(emptyList())
    private lateinit var locationModel: LocationModel
    private lateinit var recyclerViewLocation: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationModel = LocationModel(LocationRepository())
        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(LocationItemDecoration(16))


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationModel.locationList.collect{
                adapterLocation.updateListLocation(it)
            }
        }

    }

}