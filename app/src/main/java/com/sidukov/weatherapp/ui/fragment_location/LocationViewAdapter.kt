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
import com.sidukov.weatherapp.data.local.EntityLocation
import com.sidukov.weatherapp.domain.Location
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationViewAdapter(
    private var list: List<EntityLocation>,
    private var listener: OnWeatherCardClickListener
) : RecyclerView.Adapter<LocationViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_view_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onWeatherCardClicked(list[position].name)
        }

        holder.locationName?.text = list[position].name
        if (list[position].checkBoolean) {
            holder.location?.text = holder.location?.context?.getString(R.string.location)
            holder.imageCheck?.setImageResource(R.drawable.ic_check)
            holder.imageGps?.setImageResource(R.drawable.ic_gps)
        } else {
            holder.location?.visibility = View.GONE
            holder.imageCheck?.visibility = View.GONE
            holder.imageGps?.visibility = View.GONE
        }
        holder.currentTemperature?.text = list[position].temperature.toString()
        holder.currentDate?.text = list[position].date
        holder.imageWeather?.setImageResource(list[position].image)
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

    fun updateListLocation(newList: List<EntityLocation>) {
        list = newList
        notifyDataSetChanged()
    }


//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun convertToDate(): String {
//        val formatterDayMonthLocation = DateTimeFormatter.ofPattern("dd-MM")
//        val currentDayMonthLocation = LocalDateTime.now().format(formatterDayMonthLocation)
//        return currentDayMonthLocation.toString()
//    }

}

@FunctionalInterface
interface OnWeatherCardClickListener {
    fun onWeatherCardClicked(locationName: String)
}