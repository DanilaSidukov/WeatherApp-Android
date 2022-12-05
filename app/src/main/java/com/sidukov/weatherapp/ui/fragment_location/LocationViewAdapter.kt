package com.sidukov.weatherapp.ui.fragment_location

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sidukov.weatherapp.R
import com.sidukov.weatherapp.domain.Location
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationViewAdapter(
    private var list: List<Location>,
    private var listener: OnWeatherCardClickListener
) : RecyclerView.Adapter<LocationViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onWeatherCardClicked()
        }

        val locationViewModel = list[position]
        holder.locationName?.text = list[position].name
        if (locationViewModel.shouldShowLocation) {
            holder.location?.text = holder.location?.context?.getString(R.string.location)
        } else {
            holder.location?.visibility = View.GONE
        }
        if (locationViewModel.shouldShowCheckImage) {
            holder.imageCheck?.setImageResource(R.drawable.ic_check)
        } else {
            holder.imageCheck?.visibility = View.GONE
        }
        if (locationViewModel.shouldShowGpsImage) {
            holder.imageGps?.setImageResource(R.drawable.ic_gps)
        } else {
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = list[position].currentTemperature
        holder.currentDate?.text = convertToDate()
        holder.imageWeather?.setImageResource(locationViewModel.weatherImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val locationName: TextView? = row.findViewById(R.id.text_location_name)
        val location: TextView? = row.findViewById(R.id.text_location)
        val imageCheck: ImageView? = row.findViewById(R.id.image_check)
        val imageGps: ImageView? = row.findViewById(R.id.image_gps)
        val currentTemperature: TextView? = row.findViewById(R.id.current_temperature_location)
        val currentDate: TextView? = row.findViewById(R.id.date_location)
        val imageWeather: ImageView? = row.findViewById(R.id.image_weather_location)
    }

    fun updateListLocation(newList: List<Location>) {
        list = newList
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertToDate(): String {
        val formatterDayMonthLocation = DateTimeFormatter.ofPattern("dd-MM")
        val currentDayMonthLocation = LocalDateTime.now().format(formatterDayMonthLocation)
        return currentDayMonthLocation.toString()
    }

}

@FunctionalInterface
interface OnWeatherCardClickListener {
    fun onWeatherCardClicked()
}