package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.LocationDao
import com.sidukov.weatherapp.data.remote.LocationRepository
import com.sidukov.weatherapp.ui.WeatherApplication
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecoration
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.view.*

class LocationFragment: Fragment(), OnWeatherCardClickListener {
    private val adapterLocation = LocationViewAdapter(emptyList(), this)
    private lateinit var locationModel: LocationModel
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

        locationModel = LocationModel(
            LocationRepository(
                locationDao = WeatherApplication.database.daoLocation()
            )
        )
        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecoration(16))

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationModel.locationList.collect{
                adapterLocation.updateListLocation(it)
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
                dialogOpened.dismiss()
            }
            locationDialogView.button_cancel.setOnClickListener {
                dialogOpened.dismiss()
            }
        }

    }

    override fun onWeatherCardClicked() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, WeatherFragment())
            ?.commit()
    }
}