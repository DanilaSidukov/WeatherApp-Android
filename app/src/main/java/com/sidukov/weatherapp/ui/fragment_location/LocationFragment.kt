package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecorationLocation
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.custom_dialog_enter.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationFragment : Fragment(), OnWeatherCardClickListener, OnWeatherCardLongClickListener {

    private val adapterLocation = LocationViewAdapter(emptyList(), this, this)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var recyclerViewLocation: RecyclerView

    private lateinit var buttonOpenDialog: Button

    private lateinit var locationDialogView: View
    private lateinit var locationDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationViewModel = LocationViewModel(
            LocationRepository(
                locationDao = WeatherApplication.database.daoLocation(),
                EntityLocation(
                    0,
                    "",
                    "",
                    0,
                    0,
                    false,
                )
            )
        )
        locationViewModel.getLocationDataBase()

        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecorationLocation(16))

        fun updateLocationAdapter(locationList: List<EntityLocation>) {
            adapterLocation.submitList(locationList.toMutableList())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationViewModel.locationList.collect {
                if (it.isEmpty()) return@collect
                updateLocationAdapter(it)
            }
        }

        buttonOpenDialog = view.findViewById(R.id.button_add_location)

        buttonOpenDialog.setOnClickListener {
            locationDialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_dialog_enter, null, false)
            locationDialog = AlertDialog.Builder(requireContext())
                .setView(locationDialogView)

            if (locationDialogView.parent != null) {
                (locationDialogView.parent as ViewGroup).removeView(locationDialogView)
            }

            val dialogOpened = locationDialog.show()
            locationDialogView.button_enter.setOnClickListener {
                if (locationDialogView.edit_enter_location.text.toString().isNotEmpty()) {

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
                        weatherViewModel.todayStateFlow.collect {
                            if (it.date == "Error") {
                                Toast.makeText(requireContext(),
                                    "Error! Can't provide forecast for this location!",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.container,
                                        WeatherFragment(locationDialogView.edit_enter_location.text.toString()))
                                    ?.commit()
                                locationViewModel.addLocation()
                                dialogOpened.dismiss()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Type: city, country", Toast.LENGTH_SHORT)
                        .show()
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

    override fun onWeatherCardLongClickListener(locationItem: EntityLocation) {
        val locationDeleteDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_dialog_delete, null, false)
        val locationDeleteDialog = AlertDialog.Builder(requireContext())
            .setView(locationDeleteDialogView)

        if (locationDeleteDialogView.parent != null) {
            (locationDeleteDialogView.parent as ViewGroup).removeView(locationDeleteDialogView)
        }

        val deleteDialogOpen = locationDeleteDialog.show()
        locationDeleteDialogView.button_yes.setOnClickListener {
            locationViewModel = LocationViewModel(
                LocationRepository(
                    locationDao = WeatherApplication.database.daoLocation(),
                    locationItem
                )
            )
            locationViewModel.deleteItem()
            adapterLocation.deleteCurrentItem(locationItem)
            deleteDialogOpen.dismiss()
        }
        locationDeleteDialogView.button_no.setOnClickListener {
            deleteDialogOpen.dismiss()
        }
    }
}