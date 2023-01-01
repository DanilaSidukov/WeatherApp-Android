package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecorationLocation
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*

class LocationFragment: Fragment(), OnWeatherCardClickListener {

    private val adapterLocation = LocationViewAdapter(emptyList(), this)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var recyclerViewLocation: RecyclerView

    private lateinit var buttonOpenDialog: Button

    private lateinit var locationDialogView: View
    private lateinit var locationDialog: AlertDialog.Builder


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_location, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel = LocationViewModel(
            LocationRepository(
                locationDao = WeatherApplication.database.daoLocation(),
            )
        )

        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecorationLocation(16))


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationViewModel.locationList.collect{
                if (it.isEmpty()) return@collect
                adapterLocation.updateListLocation(it.distinct())
            }
        }

        buttonOpenDialog = view.findViewById(R.id.button_add_location)

        buttonOpenDialog.setOnClickListener {
            locationDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog,  null, false)
            locationDialog = AlertDialog.Builder(requireContext())
                .setView(locationDialogView)

            if (locationDialogView.parent != null){
                (locationDialogView.parent as ViewGroup).removeView(locationDialogView)
            }


            val dialogOpened = locationDialog.show()
            locationDialogView.button_enter.setOnClickListener {
                if (locationDialogView.edit_enter_location.text.toString().isNotEmpty()){

                    val weatherViewModel = WeatherViewModel(
                        WeatherRepository(
                            APIClient.weatherApiClient,
                            APIClient.geoApiClient,
                            APIClient.aqiApiClient,
                            WeatherApplication.database.daoLocation()
                        ),
                        locationDialogView.edit_enter_location.text.toString()
                    )

                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        weatherViewModel.todayStateFlow.collect{
                            if (it.date == "Error"){
                                Toast.makeText(requireContext(),"Error! Can't provide forecast for this location!", Toast.LENGTH_SHORT).show()
                            } else {
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.container, WeatherFragment(locationDialogView.edit_enter_location.text.toString()))
                                    ?.commit()
                                locationViewModel.addLocation()
                                dialogOpened.dismiss()
                            }
                        }
                    }

                } else {
                    Toast.makeText(requireContext(),"Type: city, country", Toast.LENGTH_SHORT).show()
                }
            }
            locationDialogView.button_cancel.setOnClickListener {
                dialogOpened.dismiss()
            }
        }
    }

    override fun onWeatherCardClicked(locationName: String) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, WeatherFragment(locationName))
            ?.commit()
    }
}