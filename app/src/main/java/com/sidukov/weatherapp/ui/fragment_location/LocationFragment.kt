package com.sidukov.weatherapp.ui.fragment_location

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment

class LocationFragment: Fragment(), OnWeatherCardClickListener {
    private val adapterLocation = LocationViewAdapter(emptyList(), this)
    private lateinit var locationModel: LocationModel
    private lateinit var recyclerViewLocation: RecyclerView

    private lateinit var buttonOpenDialog: Button
    private lateinit var buttonEnterLocation: Button
    private lateinit var buttonCloseDialog: Button
    private lateinit var locationDialog: Dialog

    private lateinit var editEnterLocation: TextView

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
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecoration(16))

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationModel.locationList.collect{
                adapterLocation.updateListLocation(it)
            }
        }

        locationDialog = Dialog(requireContext())
        locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        locationDialog.setCancelable(false)
        locationDialog.setContentView(R.layout.custom_dialog)

        buttonOpenDialog = view.findViewById(R.id.button_add_location)


        buttonOpenDialog.setOnClickListener {

            buttonEnterLocation = it.findViewById(R.id.button_enter)
            buttonCloseDialog = it.findViewById(R.id.button_cancel)
            editEnterLocation = it.findViewById(R.id.edit_enter_location)

            buttonEnterLocation.setOnClickListener {
                locationDialog.dismiss()
            }

            buttonCloseDialog.setOnClickListener {
                locationDialog.dismiss()
            }
            locationDialog.show()
        }

    }

    override fun onWeatherCardClicked() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, WeatherFragment())
            ?.commit()
    }
}