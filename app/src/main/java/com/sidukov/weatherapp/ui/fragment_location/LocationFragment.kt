package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.data.local.db.EntityLocation
import com.sidukov.weatherapp.di.injectViewModel
import com.sidukov.weatherapp.ui.*
import com.sidukov.weatherapp.ui.common.BaseFragment
import com.sidukov.weatherapp.ui.common.GridLayoutItemDecorationLocation
import com.sidukov.weatherapp.ui.fragment_weather.WeatherFragment
import com.sidukov.weatherapp.ui.fragment_weather.WeatherViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog_delete.view.*
import kotlinx.android.synthetic.main.custom_dialog_add.view.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class LocationFragment () : BaseFragment(R.layout.fragment_location),
    OnWeatherCardClickListener, OnDayNightStateChanged{

    private val adapterLocation = LocationViewAdapter(emptyList(), this)

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var weatherViewModel: WeatherViewModel
    lateinit var locationViewModel: LocationViewModel

    private lateinit var recyclerViewLocation: RecyclerView
    private lateinit var textNoLocation: TextView

    private lateinit var buttonOpenDialog: Button

    private lateinit var locationDialogView: View
    private lateinit var locationDialog: AlertDialog

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

        WeatherApplication.appComponent.inject(this)

        weatherViewModel = injectViewModel(viewModelFactory)
        locationViewModel = injectViewModel(viewModelFactory)

        textNoLocation = view.findViewById(R.id.text_no_location)

        var locationFragmentEntityList: List<EntityLocation> = listOf()
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            locationViewModel.locationList.collect { listEntity ->
                if (listEntity.isEmpty()) {
                    textNoLocation.visibility = View.VISIBLE
                    return@collect
                } else {
                    textNoLocation.visibility = View.GONE
                    locationFragmentEntityList = listEntity
                    updateLocationAdapter(listEntity)
                }
            }
        }

        recyclerViewLocation = view.findViewById(R.id.recycler_view_location)
        recyclerViewLocation.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerViewLocation.adapter = adapterLocation
        recyclerViewLocation.addItemDecoration(GridLayoutItemDecorationLocation(16))

        buttonOpenDialog = view.findViewById(R.id.button_add_location)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            buttonOpenDialog.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom + 40
            }
            WindowInsetsCompat.CONSUMED
        }
        buttonOpenDialog.setOnClickListener {
            locationDialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.custom_dialog_add, null, false)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(locationDialogView)

            if (locationDialogView.parent != null) {
                (locationDialogView.parent as ViewGroup).removeView(locationDialogView)
            }

            locationDialog = dialog.show()
            locationDialogView.button_enter.setOnClickListener {
                val locationDialogString = locationDialogView.edit_enter_location.text.toString()
                if (locationDialogString.isNotEmpty()) {
                    if (locationViewModel.isNetworkConnected()) locationViewModel.requestLocation(locationDialogString)
                    else Toast.makeText(context, "Connection error! Please, check your internet connection", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Type: city, country", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            locationDialogView.button_cancel.setOnClickListener {
                locationDialog.dismiss()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            locationViewModel.cityStatus.collect {
                val position = this@LocationFragment.pagePosition
                if (!it) {
                    Toast.makeText(requireContext(),
                        "Error! Can't provide forecast for this location!",
                        Toast.LENGTH_SHORT).show()
                } else {
                    (activity as? MainActivity)?.onWeatherCardClicked()
                    fragmentReplacer.replace(position, LocationFragment())
                    fragmentReplacer.replace(position + 1,
                        WeatherFragment())
                    if (locationDialog.isShowing) locationDialog.dismiss()
                }
            }
        }
    }

    override fun onWeatherCardClicked(locationEntity: EntityLocation) {
        locationViewModel.repositoryLocation.settings.savedLocation = locationEntity.name
        fragmentReplacer.replace(this.pagePosition + 1, WeatherFragment())
        (activity as? MainActivity)?.onWeatherCardClicked()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onWeatherCardLongClickListener(locationItem: EntityLocation) {
        // val edit = locationViewModel.repositoryLocation.settings.savedLocation

        val locationDeleteDialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_dialog_delete, null, false)
        val locationDeleteDialog = AlertDialog.Builder(requireContext())
            .setView(locationDeleteDialogView)

        if (locationDeleteDialogView.parent != null) {
            (locationDeleteDialogView.parent as ViewGroup).removeView(locationDeleteDialogView)
        }

        val deleteDialogOpen = locationDeleteDialog.show()
        locationDeleteDialogView.button_yes.setOnClickListener {
            locationViewModel.repositoryLocation.settings.deleteValue()
            locationViewModel.deleteItem(locationItem)
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

    override fun onDayNightApplied(state: Int) {
        if (state == OnDayNightStateChanged.DAY) AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

}