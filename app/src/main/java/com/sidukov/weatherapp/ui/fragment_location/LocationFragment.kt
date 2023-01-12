package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.data.remote.WeatherRepository
import com.sidukov.weatherapp.data.remote.api.APIClient
import com.sidukov.weatherapp.ui.OnWeatherCardListener
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.BaseFragment
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecorationLocation
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.custom_dialog_add.view.*
import java.util.*

class LocationFragment(locationName: String, private val listener: OnWeatherCardListener, private var entityListLocation: List<EntityLocation>) : BaseFragment(R.layout.fragment_location),
    OnWeatherCardClickListener {

    private val adapterLocation = LocationViewAdapter(emptyList(), this, locationName)
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var recyclerViewLocation: RecyclerView
    private lateinit var textNoLocation: TextView

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

        updateLocationAdapter(entityListLocation)

        textNoLocation = view.findViewById(R.id.text_no_location)

        locationViewModel = LocationViewModel(
            LocationRepository(
                locationDao = WeatherApplication.database.daoLocation(),
                EntityLocation(
                    "",
                    "",
                    0,
                    0,
                )
            )
        )
        locationViewModel.getLocationDataBase()

        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecorationLocation(16))

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationViewModel.locationList.collect {
                println("LIST = $it")
                if (it.isEmpty()) {
                    textNoLocation.visibility = View.VISIBLE
                    return@collect
                } else {
                    textNoLocation.visibility = View.GONE
                    updateLocationAdapter(it)
                    entityListLocation = it
                }
            }
        }

        buttonOpenDialog = view.findViewById(R.id.button_add_location)
        buttonOpenDialog.setOnClickListener {
            locationDialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_dialog_add, null, false)
            locationDialog = AlertDialog.Builder(requireContext())
                .setView(locationDialogView)

            if (locationDialogView.parent != null) {
                (locationDialogView.parent as ViewGroup).removeView(locationDialogView)
            }

            val position = this.pagePosition

            val dialogOpened = locationDialog.show()
            locationDialogView.button_enter.setOnClickListener {
                val locationDialogString = locationDialogView.edit_enter_location.text.toString()
                if (locationDialogString.isNotEmpty()) {

                    val weatherViewModel = WeatherViewModel(
                        WeatherRepository(
                            APIClient.weatherApiClient,
                            APIClient.geoApiClient,
                            APIClient.aqiApiClient,
                            WeatherApplication.database.daoLocation(),
                            requireContext()
                        ),
                        locationDialogString
                    )
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        weatherViewModel.todayStateFlow.collect {
                            if (it.date == "Error") {
                                Toast.makeText(requireContext(),
                                    "Error! Can't provide forecast for this location!",
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                fragmentReplacer.replace(position, LocationFragment(locationDialogString,listener, entityListLocation),locationDialogString)
                                fragmentReplacer.replace(position + 1,
                                    WeatherFragment(locationDialogString),
                                    locationDialogView.edit_enter_location.text.toString())
                                listener.onWeatherCardClicked()
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

    override fun onWeatherCardClicked(locationEntity: EntityLocation) {
        fragmentReplacer.replace(this.pagePosition + 1, WeatherFragment(locationEntity.name), locationEntity.name)
        listener.onWeatherCardClicked()
        locationViewModel = LocationViewModel(
            LocationRepository(
                locationDao = WeatherApplication.database.daoLocation(),
                locationEntity
            )
        )
        locationViewModel.getLocationDataBase()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onWeatherCardLongClickListener(locationItem: EntityLocation) {
        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences("city", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()

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
            edit.clear()
            edit.apply()
            locationViewModel.deleteItem()
            adapterLocation.deleteCurrentItem(locationItem)
            deleteDialogOpen.dismiss()
        }
        locationDeleteDialogView.button_no.setOnClickListener {
            deleteDialogOpen.dismiss()
        }
    }

    private fun updateLocationAdapter(locationList: List<EntityLocation>) {
        adapterLocation.submitList(locationList.toMutableList())
    }
}